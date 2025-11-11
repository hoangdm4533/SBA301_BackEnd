package com.example.demologin.serviceImpl;

import com.example.demologin.dto.request.question.QuestionCreateRequest;
import com.example.demologin.dto.request.question.QuestionUpdateRequest;
import com.example.demologin.dto.response.QuestionResponse;
import com.example.demologin.entity.*;
import com.example.demologin.exception.exceptions.NotFoundException;
import com.example.demologin.mapper.question.QuestionMapper;
import com.example.demologin.repository.*;
import com.example.demologin.service.QuestionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepo;
    private final OptionRepository optionRepo;
    private final QuestionTypeRepository questionTypeRepo;
    private final QuestionMapper mapper;
    private final LevelRepository levelRepo;
    private final LessonRepository lessonRepo;


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
    public QuestionResponse create(QuestionCreateRequest req) {
        Lesson lesson = lessonRepo.findById(req.getLessonId())
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found: " + req.getLessonId()));

        Level level = levelRepo.findById(req.getLevelId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid levelId: " + req.getLevelId()));

        String typeCode = req.getType() == null ? null : req.getType().trim().toUpperCase();
        QuestionType type = questionTypeRepo.findByDescriptionIgnoreCase(typeCode)
                .orElseThrow(() -> new IllegalArgumentException("QuestionType not found: " + req.getType()));

        Question q = new Question();
        q.setQuestionText(req.getQuestionText());
        q.setFormula(req.getFormula());
        q.setLesson(lesson);
        q.setLevel(level);
        q.setType(type);
        q.setCreatedAt(LocalDateTime.now());
        q.setUpdatedAt(LocalDateTime.now());

        // Thêm options vào collection đang managed (KHÔNG gán list mới)
        if (req.getOptions() != null && !req.getOptions().isEmpty()) {
            List<Option> options = req.getOptions().stream()
                    .map(o -> new Option(null, null, o.getOptionText(), Boolean.TRUE.equals(o.getIsCorrect())))
                    .collect(java.util.stream.Collectors.toList());

            validateOptions(typeCode, options);

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
    public QuestionResponse update(Long questionId, QuestionUpdateRequest req) {
        Question q = questionRepo.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));

        if (req.getQuestionText() != null && !req.getQuestionText().isBlank()) {
            q.setQuestionText(req.getQuestionText().trim());
        }
        if (req.getFormula() != null) {
            q.setFormula(req.getFormula());
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
        if (req.getType() != null && !req.getType().isBlank()) {
            effectiveTypeCode = req.getType().trim().toUpperCase();
            QuestionType type = questionTypeRepo.findByDescriptionIgnoreCase(effectiveTypeCode)
                    .orElseThrow(() -> new IllegalArgumentException("QuestionType not found: " + req.getType()));
            q.setType(type);
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
        // Gỡ liên kết exam_questions để tránh lỗi FK
        questionRepo.unlinkAllExamsOfQuestion(id);

        Question q = questionRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Question not found: " + id));
        questionRepo.delete(q);
    }
}
