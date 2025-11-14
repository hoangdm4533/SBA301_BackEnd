package com.example.demologin.service.impl;

import com.example.demologin.config.GeminiConfig;
import com.example.demologin.dto.request.ai.QuestionGenerate;
import com.example.demologin.dto.request.question.QuestionCreateRequest;
import com.example.demologin.dto.request.question.QuestionUpdateRequest;
import com.example.demologin.dto.response.QuestionResponse;
import com.example.demologin.entity.*;
import com.example.demologin.enums.QuestionStatus;
import com.example.demologin.exception.exceptions.NotFoundException;
import com.example.demologin.mapper.question.QuestionMapper;
import com.example.demologin.repository.*;
import com.example.demologin.service.CloudinaryService;
import com.example.demologin.service.QuestionService;
import com.google.genai.errors.ApiException;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepo;
    private final QuestionTypeRepository questionTypeRepo;
    private final QuestionMapper mapper;
    private final LevelRepository levelRepo;
    private final LessonRepository lessonRepo;
    private final GeminiConfig geminiConfig;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponse> list(int page, int size) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return questionRepo.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionResponse get(Long id) {
        Question q = questionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));
        return mapper.toResponse(q);
    }

    @Override
    @Transactional
    public QuestionResponse create(QuestionCreateRequest req, MultipartFile imageFile) {
        Lesson lesson = lessonRepo.findById(req.getLessonId())
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found: " + req.getLessonId()));

        Level level = levelRepo.findById(req.getLevelId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid levelId: " + req.getLevelId()));

        QuestionType type = questionTypeRepo.findById(req.getTypeId())
                .orElseThrow(() -> new IllegalArgumentException("QuestionType not found: " + req.getTypeId()));

        Question q = new Question();
        q.setQuestionText(req.getQuestionText());
        q.setLesson(lesson);
        q.setLevel(level);
        q.setType(type);
        q.setStatus(QuestionStatus.ACTIVE);
        q.setCreatedAt(LocalDateTime.now());
        q.setUpdatedAt(LocalDateTime.now());

        // Upload image to Cloudinary if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                Map<String, Object> uploadResult = cloudinaryService.uploadImage(imageFile, "question_images");
                q.setImageUrl((String) uploadResult.get("file_url"));
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image: " + e.getMessage());
            }
        }

        // Thêm options vào collection đang managed (KHÔNG gán list mới)
        if (req.getOptions() != null && !req.getOptions().isEmpty()) {
            List<Option> options = req.getOptions().stream()
                    .map(o -> new Option(null, null, o.getOptionText(), Boolean.TRUE.equals(o.getIsCorrect())))
                    .collect(java.util.stream.Collectors.toList());

            validateOptions(type.getDescription(), options);

            for (Option o : options) {
                q.addOption(o); // add vào collection & setQuestion(this)
            }
        }

        // Chỉ cần save question; cascade=ALL sẽ lưu Option
        Question saved = questionRepo.save(q);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public QuestionResponse update(Long questionId, QuestionUpdateRequest req, MultipartFile imageFile) {
        Question q = questionRepo.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));

        if (req.getQuestionText() != null && !req.getQuestionText().isBlank()) {
            q.setQuestionText(req.getQuestionText().trim());
        }
        
        // Handle image upload/update
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // Delete old image if exists
                if (q.getImageUrl() != null && !q.getImageUrl().isEmpty()) {
                    String oldPublicId = extractPublicIdFromUrl(q.getImageUrl());
                    if (oldPublicId != null) {
                        cloudinaryService.deleteImage(oldPublicId);
                    }
                }
                // Upload new image
                Map<String, Object> uploadResult = cloudinaryService.uploadImage(imageFile, "question_images");
                q.setImageUrl((String) uploadResult.get("file_url"));
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload/delete image: " + e.getMessage());
            }
        }
        if (req.getLessonId() != null) {
            Lesson lesson = lessonRepo.findById(req.getLessonId())
                    .orElseThrow(() -> new IllegalArgumentException("Lesson not found: " + req.getLessonId()));
            q.setLesson(lesson);
        }
        if (req.getLevelId() != null) {
            Level level = levelRepo.findById(req.getLevelId())
                    .orElseThrow(() -> new IllegalArgumentException("Level not found: " + req.getLevelId()));
            q.setLevel(level);
        }

        String effectiveTypeCode = null;
        if (req.getTypeId() != null) {
            QuestionType type = questionTypeRepo.findById(req.getTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("QuestionType not found: " + req.getTypeId()));
            q.setType(type);
            effectiveTypeCode = type.getDescription();
        } else if (q.getType() != null) {
            effectiveTypeCode = q.getType().getDescription();
        }

        q.setUpdatedAt(LocalDateTime.now());

        // Replace-all options đúng cách với orphanRemoval
        if (req.getOptions() != null) {
            // Build list mới (mutable, chưa set question)
            List<Option> newOptions = req.getOptions().stream()
                    .map(o -> new Option(null, null, o.getOptionText(), Boolean.TRUE.equals(o.getIsCorrect())))
                    .collect(java.util.stream.Collectors.toList());

            if (effectiveTypeCode != null) {
                validateOptions(effectiveTypeCode.toUpperCase(), newOptions);
            }

            // 1) Clear trên collection HIỆN CÓ (KHÔNG set list mới)
            q.clearOptions(); // null back-ref + clear()

            // 2) Add từng option vào collection managed (giữ nguyên instance)
            for (Option o : newOptions) {
                q.addOption(o); // setQuestion(this)
            }
        }

        // Không cần optionRepo.deleteAll/saveAll nếu cascade=ALL+orphanRemoval=true
        Question saved = questionRepo.save(q);
        return mapper.toResponse(saved);
    }

    private void validateOptions(String typeCode, List<Option> options) {
        if (typeCode == null) return;

        long correctCount = options.stream().filter(Option::getIsCorrect).count();

        switch (typeCode) {
            case "MCQ_SINGLE" -> {
                if (correctCount != 1)
                    throw new IllegalArgumentException("MCQ_SINGLE requires exactly 1 correct option.");
                if (options.size() < 2)
                    throw new IllegalArgumentException("MCQ_SINGLE should have at least 2 options.");
            }
            case "MCQ_MULTI" -> {
                if (correctCount < 1)
                    throw new IllegalArgumentException("MCQ_MULTI requires at least 1 correct option.");
                if (options.size() < 2)
                    throw new IllegalArgumentException("MCQ_MULTI should have at least 2 options.");
            }
            case "TRUE_FALSE" -> {
                if (options.size() != 2)
                    throw new IllegalArgumentException("TRUE_FALSE requires exactly 2 options (True/False).");
                if (correctCount != 1)
                    throw new IllegalArgumentException("TRUE_FALSE requires exactly 1 correct option.");
            }
            default -> { /* ignore others */ }
        }
    }


    @Override
    @Transactional
    public void delete(Long id) {
        Question q = questionRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Question not found: " + id));


        if (q.getStatus() != QuestionStatus.INACTIVE) {
            throw new IllegalArgumentException(
                "Chỉ có thể xóa câu hỏi đã được archive. "
            );
        }

        // Gỡ liên kết exam_questions để tránh lỗi FK
        questionRepo.unlinkAllExamsOfQuestion(id);

        questionRepo.delete(q);
    }

    @Override
    public CompletableFuture<String> generateQuestion(QuestionGenerate req) {
        String sysIns = buildSystemInstruction(req);
        GenerateContentConfig config = GenerateContentConfig.builder()
                .temperature(0.2f)
                .systemInstruction(Content.fromParts(Part.fromText(sysIns)))
                .build();

        String userPrompt = buildUserPrompt(req);
        Content content = Content.fromParts(Part.fromText(userPrompt));

        ExecutorService executor = Executors.newSingleThreadExecutor();

        return CompletableFuture.supplyAsync(() -> {
                    int retryCount = 0;
                    while (retryCount < 3) {
                        try {
                            GenerateContentResponse res = geminiConfig.generate(
                                    "gemini-2.5-flash",
                                    content,
                                    config
                            );
                            return res.text();
                        } catch (ApiException e) {
                            if (e.getMessage().contains("503")) {
                                retryCount++;
                                log.info("Lần {} bị lỗi 503, đang retry...", retryCount);
                                try {
                                    // Exponential backoff strategy
                                    TimeUnit.SECONDS.sleep((long) Math.pow(2, retryCount));
                                } catch (InterruptedException ie) {
                                    Thread.currentThread().interrupt();
                                    throw new RuntimeException("Thread bị gián đoạn", ie);
                                }
                            } else {
                                throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
                            }
                        }
                    }
                    throw new RuntimeException("Hệ thống AI đang quá tải. Vui lòng thử lại sau.");
                }, executor)
                .whenComplete((result, ex) -> executor.shutdown());
    }

    private String buildSystemInstruction(QuestionGenerate req) {
        int questionCount = req.getQuantity() != null ? req.getQuantity() : 1;

        String difficulty = "";
        if (req.getLevelId() != null) {
            difficulty = levelRepo.findById(req.getLevelId())
                    .map(Level::getDifficulty)
                    .orElse("");
        }
        String type = "";
        if (req.getQuestionTypeId() != null) {
            type = questionTypeRepo.findById(req.getQuestionTypeId())
                    .map(QuestionType::getDescription)
                    .orElse("");
        }

        return """
        Bạn là hệ thống tạo câu hỏi trắc nghiệm chính xác cao môn Toán học lớp 10–12.
        Sinh %d câu hỏi dựa trên dữ liệu đầu vào.

        type: %s
        difficulty: %s

        Quy tắc:
        - Mỗi câu hỏi có 2–4 phương án.
        - Mỗi câu có thể có nhiều phương án đúng.
        - Ở phương án đúng, thêm chữ (đúng) trong ngoặc.
        - Cuối mỗi câu hỏi phải ghi thêm:
          (type: {type}, difficulty: {difficulty})

        Format trả về:
        1. {question} (type: {type}, difficulty: {difficulty})
        A. {option 1}
        B. {option 2 (đúng nếu đúng)}
        C. {option 3}
        D. {option 4}
        """
                .formatted(
                        questionCount,
                        type,
                        difficulty
                );
    }

    private String buildUserPrompt(QuestionGenerate req) {
        int questionCount = req.getQuantity() != null ? req.getQuantity() : 1;

        String difficulty = "";
        if (req.getLevelId() != null) {
            difficulty = levelRepo.findById(req.getLevelId())
                    .map(Level::getDifficulty)
                    .orElse("");
        }

        String type = "";
        if (req.getQuestionTypeId() != null) {
            type = questionTypeRepo.findById(req.getQuestionTypeId())
                    .map(QuestionType::getDescription)
                    .orElse("");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Sinh ").append(questionCount)
                .append(" câu hỏi theo loại ").append(type).append(".\n");

        if (!difficulty.isBlank()) {
            sb.append("Mức độ: ").append(difficulty).append(".\n");
        }

        sb.append("""
        Mỗi câu hỏi có 2–4 phương án và chỉ có 1 phương án đúng.
        Cuối mỗi câu hỏi phải ghi thêm (type: {type}, difficulty: {difficulty}).
        Xuất ra đúng format đánh số câu hỏi và các lựa chọn A/B/C/D.
        """);

        return sb.toString();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponse> listByLevel(Long levelId, int page, int size) {
        Level level = levelRepo.findById(levelId)
                .orElseThrow(() -> new NotFoundException("Level not found: " + levelId));

        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return questionRepo.findByLevel(level, pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponse> listByType(Long typeId, int page, int size) {
        QuestionType type = questionTypeRepo.findById(typeId)
                .orElseThrow(() -> new NotFoundException("QuestionType not found: " + typeId));

        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return questionRepo.findByType(type, pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponse> listByMatrix(Long matrixId, int page, int size) {
        // Kiểm tra matrix có tồn tại không (optional, có thể bỏ nếu không cần)
        // matrixRepo.findById(matrixId).orElseThrow(() -> new NotFoundException("Matrix not found: " + matrixId));

        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return questionRepo.findByMatrixId(matrixId, pageable).map(mapper::toResponse);
    }


    @Override
    @Transactional
    public List<QuestionResponse> saveQuestionsAI(List<QuestionCreateRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("Câu hỏi không được trống");
        }

        List<Question> questions = new ArrayList<>();

        for (QuestionCreateRequest req : requests) {
            // --- Validate & load các entity liên quan ---
            Lesson lesson = lessonRepo.findById(req.getLessonId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài học"));

            QuestionType type = questionTypeRepo.findById(req.getTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy loại câu hỏi"));

            Level level = null;
            if (req.getLevelId() != null) {
                level = levelRepo.findById(req.getLevelId())
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy cấp độ câu hỏi"));
            }

            // ---Tạo đối tượng Question ---
            Question q = new Question();
            q.setQuestionText(req.getQuestionText());
            q.setType(type);
            q.setLesson(lesson);
            q.setLevel(level);
            q.setCreatedAt(LocalDateTime.now());
            q.setUpdatedAt(LocalDateTime.now());

            // ---  Map OptionRequest -> Option ---
            if (req.getOptions() != null && !req.getOptions().isEmpty()) {
                List<Option> options = req.getOptions().stream()
                        .map(optReq -> mapper.buildOption(q, optReq))
                        .collect(Collectors.toList());
                q.setOptions(options);
            }

            questions.add(q);
        }

        // ---  Lưu tất cả vào DB ---
        List<Question> savedQuestions = questionRepo.saveAll(questions);

        // --- Convert sang Response ---
        return savedQuestions.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponse> listByStatus(String status, int page, int size) {
        try {
            QuestionStatus questionStatus = QuestionStatus.valueOf(status.toUpperCase());
            var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
            return questionRepo.findByStatus(questionStatus, pageable).map(mapper::toResponse);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status. Must be ACTIVE or ARCHIVED");
        }
    }

    @Override
    @Transactional
    public QuestionResponse changeStatus(Long id) {
        Question question = questionRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Question not found: " + id));

        QuestionStatus currentStatus = question.getStatus();

        // Nếu đang ACTIVE và muốn chuyển sang ARCHIVED
        if (currentStatus == QuestionStatus.ACTIVE) {
            // Kiểm tra câu hỏi có trong exam nào không
            boolean isUsedInExam = !questionRepo.findExamsByQuestionId(id).isEmpty();

            if (isUsedInExam) {
                throw new IllegalArgumentException(
                    "Không thể archive câu hỏi này vì đã được sử dụng trong đề thi."
                );
            }
            question.setStatus(QuestionStatus.INACTIVE);
        }
        // Nếu đang ARCHIVED và muốn chuyển sang ACTIVE
        else if (currentStatus == QuestionStatus.INACTIVE) {
            question.setStatus(QuestionStatus.ACTIVE);
        }

        question.setUpdatedAt(LocalDateTime.now());

        Question saved = questionRepo.save(question);
        return mapper.toResponse(saved);
    }

    private String extractPublicIdFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }
        try {
            // Cloudinary URL format: https://res.cloudinary.com/<cloud_name>/image/upload/v<version>/<folder>/<public_id>.<extension>
            String[] parts = imageUrl.split("/");
            if (parts.length >= 2) {
                String fileWithExt = parts[parts.length - 1];
                String folderAndFile = parts[parts.length - 2] + "/" + fileWithExt;
                // Remove extension
                return folderAndFile.substring(0, folderAndFile.lastIndexOf('.'));
            }
        } catch (Exception e) {
            // Log and continue
        }
        return null;
    }
}
