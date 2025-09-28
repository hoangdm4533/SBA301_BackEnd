package com.example.demologin.serviceImpl;

import com.example.demologin.dto.request.exam_template.ExamTemplateRequest;
import com.example.demologin.dto.request.exam_template.AddQuestionToExamRequest;
import com.example.demologin.dto.response.ExamTemplateResponse;
import com.example.demologin.dto.response.ExamQuestionResponse;
import com.example.demologin.entity.*;
import com.example.demologin.exception.exceptions.BadRequestException;
import com.example.demologin.exception.exceptions.NotFoundException;
import com.example.demologin.repository.*;
import com.example.demologin.service.ExamTemplateService;
import com.example.demologin.utils.AccountUtils;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ExamTemplateServiceImpl implements ExamTemplateService {
    
    private final ExamTemplateRepository examTemplateRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final LevelRepository levelRepository;
    private final QuestionRepository questionRepository;
    private final AccountUtils accountUtils;

    private ExamTemplateResponse mapToResponse(ExamTemplate examTemplate) {
        return ExamTemplateResponse.builder()
                .id(examTemplate.getId())
                .title(examTemplate.getTitle())
                .description(examTemplate.getDescription())
                .levelId(examTemplate.getLevel().getId())
                .levelName(examTemplate.getLevel().getName())
                .difficulty(examTemplate.getDifficulty())
                .status(examTemplate.getStatus())
                .duration(examTemplate.getDuration())
                .totalQuestions(examTemplate.getTotalQuestions())
                .totalPoints(examTemplate.getTotalPoints())
                .createdBy(examTemplate.getCreatedBy().getUsername())
                .updatedBy(examTemplate.getUpdatedBy() != null ? examTemplate.getUpdatedBy().getUsername() : null)
                .approvedBy(examTemplate.getApprovedBy() != null ? examTemplate.getApprovedBy().getUsername() : null)
                .createdAt(examTemplate.getCreatedAt())
                .updatedAt(examTemplate.getUpdatedAt())
                .approvedAt(examTemplate.getApprovedAt())
                .questions(examTemplate.getExamQuestions().stream()
                        .map(this::mapExamQuestionToResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    private ExamQuestionResponse mapExamQuestionToResponse(ExamQuestion examQuestion) {
        return ExamQuestionResponse.builder()
                .id(examQuestion.getId())
                .examTemplateId(examQuestion.getExamTemplate().getId())
                .questionId(examQuestion.getQuestion().getId())
                .questionText(examQuestion.getQuestion().getQuestionText())
                .questionType(examQuestion.getQuestion().getType())
                .questionDifficulty(examQuestion.getQuestion().getDifficulty())
                .questionOrder(examQuestion.getQuestionOrder())
                .points(examQuestion.getPoints())
                .note(examQuestion.getNote())
                .createdAt(examQuestion.getCreatedAt())
                .build();
    }

    @Override
    public ExamTemplateResponse createExamTemplate(ExamTemplateRequest request) {
        // Validate title uniqueness
        if (examTemplateRepository.existsByTitleIgnoreCase(request.getTitle())) {
            throw new BadRequestException("Exam template với tiêu đề này đã tồn tại");
        }

        // Validate level existence
        if (request.getLevelId() == null) {
            throw new BadRequestException("Level ID không được để trống");
        }
        
        Level level = levelRepository.findById(request.getLevelId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy level với id " + request.getLevelId()));

        User currentUser = accountUtils.getCurrentUser();

        ExamTemplate examTemplate = ExamTemplate.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .level(level)
                .difficulty(request.getDifficulty())
                .duration(request.getDuration())
                .totalQuestions(request.getTotalQuestions())
                .totalPoints(request.getTotalPoints())
                .createdBy(currentUser)
                .updatedBy(currentUser)
                .build();

        ExamTemplate saved = examTemplateRepository.save(examTemplate);
        return mapToResponse(saved);
    }

    @Override
    public ExamTemplateResponse getExamTemplateById(Long id) {
        ExamTemplate examTemplate = examTemplateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy exam template với id " + id));
        return mapToResponse(examTemplate);
    }

    @Override
    public List<ExamTemplateResponse> getAllExamTemplates() {
        return examTemplateRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ExamTemplateResponse> getAllExamTemplates(Pageable pageable) {
        return examTemplateRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public Page<ExamTemplateResponse> getExamTemplatesByLevel(Long levelId, Pageable pageable) {
        return examTemplateRepository.findByLevelIdAndStatus(levelId, "PUBLISHED", pageable)
                .map(this::mapToResponse);
    }

    @Override
    public Page<ExamTemplateResponse> getExamTemplatesByStatus(String status, Pageable pageable) {
        return examTemplateRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public Page<ExamTemplateResponse> searchExamTemplates(String keyword, Pageable pageable) {
        return examTemplateRepository.findByKeyword(keyword, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public Page<ExamTemplateResponse> searchExamTemplatesByLevel(Long levelId, String keyword, Pageable pageable) {
        return examTemplateRepository.findByLevelIdAndKeyword(levelId, keyword, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public ExamTemplateResponse updateExamTemplate(Long id, ExamTemplateRequest request) {
        ExamTemplate examTemplate = examTemplateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy exam template với id " + id));

        if (!examTemplate.getTitle().equalsIgnoreCase(request.getTitle()) &&
            examTemplateRepository.existsByTitleIgnoreCase(request.getTitle())) {
            throw new BadRequestException("Exam template với tiêu đề này đã tồn tại");
        }

        if ("PUBLISHED".equals(examTemplate.getStatus()) || "ARCHIVED".equals(examTemplate.getStatus())) {
            throw new BadRequestException("Không thể cập nhật exam template đã được publish hoặc archive");
        }

        Level level = levelRepository.findById(request.getLevelId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy level với id " + request.getLevelId()));

        User currentUser = accountUtils.getCurrentUser();

        examTemplate.setTitle(request.getTitle());
        examTemplate.setDescription(request.getDescription());
        examTemplate.setLevel(level);
        examTemplate.setDifficulty(request.getDifficulty());
        examTemplate.setDuration(request.getDuration());
        examTemplate.setTotalQuestions(request.getTotalQuestions());
        examTemplate.setTotalPoints(request.getTotalPoints());
        examTemplate.setUpdatedBy(currentUser);

        ExamTemplate updated = examTemplateRepository.save(examTemplate);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteExamTemplate(Long id) {
        ExamTemplate examTemplate = examTemplateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy exam template với id " + id));

        if ("PUBLISHED".equals(examTemplate.getStatus())) {
            throw new BadRequestException("Không thể xóa exam template đã được publish");
        }

        examTemplateRepository.delete(examTemplate);
    }

    @Override
    @Transactional
    public ExamQuestionResponse addQuestionToExam(Long examTemplateId, AddQuestionToExamRequest request) {
        ExamTemplate examTemplate = examTemplateRepository.findById(examTemplateId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy exam template với id " + examTemplateId));

        if ("PUBLISHED".equals(examTemplate.getStatus()) || "ARCHIVED".equals(examTemplate.getStatus())) {
            throw new BadRequestException("Không thể thêm câu hỏi vào exam template đã được publish hoặc archive");
        }

        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy question với id " + request.getQuestionId()));

        if (examQuestionRepository.existsByExamTemplateAndQuestion(examTemplate, question)) {
            throw new BadRequestException("Câu hỏi này đã có trong exam template");
        }

        if (examQuestionRepository.existsByExamTemplateAndQuestionOrder(examTemplate, request.getQuestionOrder())) {
            throw new BadRequestException("Thứ tự câu hỏi này đã được sử dụng");
        }

        ExamQuestion examQuestion = ExamQuestion.builder()
                .examTemplate(examTemplate)
                .question(question)
                .questionOrder(request.getQuestionOrder())
                .points(request.getPoints())
                .note(request.getNote())
                .build();

        ExamQuestion saved = examQuestionRepository.save(examQuestion);

        // Update exam template statistics
        updateExamTemplateStatistics(examTemplate);

        return mapExamQuestionToResponse(saved);
    }

    @Override
    @Transactional
    public void removeQuestionFromExam(Long examTemplateId, Long questionId) {
        ExamTemplate examTemplate = examTemplateRepository.findById(examTemplateId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy exam template với id " + examTemplateId));

        if ("PUBLISHED".equals(examTemplate.getStatus()) || "ARCHIVED".equals(examTemplate.getStatus())) {
            throw new BadRequestException("Không thể xóa câu hỏi khỏi exam template đã được publish hoặc archive");
        }

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy question với id " + questionId));

        ExamQuestion examQuestion = examQuestionRepository.findByExamTemplateAndQuestion(examTemplate, question)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy câu hỏi này trong exam template"));

        examQuestionRepository.delete(examQuestion);

        // Update exam template statistics
        updateExamTemplateStatistics(examTemplate);
    }

    @Override
    @Transactional
    public void updateQuestionInExam(Long examTemplateId, Long questionId, AddQuestionToExamRequest request) {
        ExamTemplate examTemplate = examTemplateRepository.findById(examTemplateId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy exam template với id " + examTemplateId));

        if ("PUBLISHED".equals(examTemplate.getStatus()) || "ARCHIVED".equals(examTemplate.getStatus())) {
            throw new BadRequestException("Không thể cập nhật câu hỏi trong exam template đã được publish hoặc archive");
        }

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy question với id " + questionId));

        ExamQuestion examQuestion = examQuestionRepository.findByExamTemplateAndQuestion(examTemplate, question)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy câu hỏi này trong exam template"));

        // Check if new order is already used by another question
        if (!examQuestion.getQuestionOrder().equals(request.getQuestionOrder()) &&
            examQuestionRepository.existsByExamTemplateAndQuestionOrder(examTemplate, request.getQuestionOrder())) {
            throw new BadRequestException("Thứ tự câu hỏi này đã được sử dụng");
        }

        examQuestion.setQuestionOrder(request.getQuestionOrder());
        examQuestion.setPoints(request.getPoints());
        examQuestion.setNote(request.getNote());

        examQuestionRepository.save(examQuestion);

        // Update exam template statistics
        updateExamTemplateStatistics(examTemplate);
    }

    @Override
    public List<ExamQuestionResponse> getQuestionsInExam(Long examTemplateId) {
        List<ExamQuestion> examQuestions = examQuestionRepository.findByExamTemplateIdOrderByQuestionOrder(examTemplateId);
        return examQuestions.stream()
                .map(this::mapExamQuestionToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void reorderQuestionsInExam(Long examTemplateId, List<Long> questionIds) {
        ExamTemplate examTemplate = examTemplateRepository.findById(examTemplateId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy exam template với id " + examTemplateId));

        if ("PUBLISHED".equals(examTemplate.getStatus()) || "ARCHIVED".equals(examTemplate.getStatus())) {
            throw new BadRequestException("Không thể sắp xếp lại câu hỏi trong exam template đã được publish hoặc archive");
        }

        for (int i = 0; i < questionIds.size(); i++) {
            Long questionId = questionIds.get(i);
            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy question với id " + questionId));

            ExamQuestion examQuestion = examQuestionRepository.findByExamTemplateAndQuestion(examTemplate, question)
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy câu hỏi này trong exam template"));

            examQuestion.setQuestionOrder(i + 1);
            examQuestionRepository.save(examQuestion);
        }
    }

    @Override
    @Transactional
    public void publishExamTemplate(Long id) {
        ExamTemplate examTemplate = examTemplateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy exam template với id " + id));

        if ("PUBLISHED".equals(examTemplate.getStatus())) {
            throw new BadRequestException("Exam template đã được publish");
        }

        long questionCount = examQuestionRepository.countByExamTemplate(examTemplate);
        if (questionCount == 0) {
            throw new BadRequestException("Không thể publish exam template không có câu hỏi nào");
        }

        User currentUser = accountUtils.getCurrentUser();
        examTemplate.setStatus("PUBLISHED");
        examTemplate.setUpdatedBy(currentUser);
        examTemplateRepository.save(examTemplate);
    }

    @Override
    @Transactional
    public void archiveExamTemplate(Long id) {
        ExamTemplate examTemplate = examTemplateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy exam template với id " + id));

        User currentUser = accountUtils.getCurrentUser();
        examTemplate.setStatus("ARCHIVED");
        examTemplate.setUpdatedBy(currentUser);
        examTemplateRepository.save(examTemplate);
    }

    @Override
    @Transactional
    public void approveExamTemplate(Long id) {
        ExamTemplate examTemplate = examTemplateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy exam template với id " + id));

        if ("ARCHIVED".equals(examTemplate.getStatus())) {
            throw new BadRequestException("Không thể approve exam template đã được archive");
        }

        User currentUser = accountUtils.getCurrentUser();
        examTemplate.setApprovedBy(currentUser);
        examTemplate.setApprovedAt(LocalDateTime.now());
        examTemplate.setUpdatedBy(currentUser);
        examTemplateRepository.save(examTemplate);
    }

    private void updateExamTemplateStatistics(ExamTemplate examTemplate) {
        long questionCount = examQuestionRepository.countByExamTemplate(examTemplate);
        Double totalPoints = examQuestionRepository.sumPointsByExamTemplateId(examTemplate.getId());

        examTemplate.setTotalQuestions((int) questionCount);
        examTemplate.setTotalPoints(totalPoints != null ? totalPoints : 0.0);
        examTemplateRepository.save(examTemplate);
    }
}