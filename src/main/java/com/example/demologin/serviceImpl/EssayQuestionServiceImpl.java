package com.example.demologin.serviceImpl;

import com.example.demologin.dto.request.EssayQuestionRequest;
import com.example.demologin.dto.response.EssayQuestionResponse;
import com.example.demologin.dto.response.PageResponse;
import com.example.demologin.entity.EssayQuestion;
import com.example.demologin.entity.User;
import com.example.demologin.enums.QuestionStatus;
import com.example.demologin.repository.EssayQuestionRepository;
import com.example.demologin.repository.UserRepository;
import com.example.demologin.service.EssayQuestionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EssayQuestionServiceImpl implements EssayQuestionService {
    private final EssayQuestionRepository questionRepo;
    private final UserRepository userRepo;

    @Override
    @Transactional
    public EssayQuestionResponse createQuestion(EssayQuestionRequest request, Long teacherId) {
        User teacher = userRepo.findById(teacherId)
            .orElseThrow(() -> new EntityNotFoundException("Teacher not found"));

        EssayQuestion question = EssayQuestion.builder()
            .prompt(request.getPrompt())
            .rubric(request.getRubric())
            .timeLimitMinutes(request.getTimeLimitMinutes())
            .maxScore(request.getMaxScore())
            .status(QuestionStatus.ACTIVE)
            .createdBy(teacher)
            .build();

        question = questionRepo.save(question);
        log.info("Teacher {} created essay question {}", teacherId, question.getId());

        return mapToResponse(question);
    }

    @Override
    @Transactional
    public EssayQuestionResponse updateQuestion(Long id, EssayQuestionRequest request, Long teacherId) {
        EssayQuestion question = questionRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Question not found"));

        // Check ownership
        if (!question.getCreatedBy().getUserId().equals(teacherId)) {
            throw new SecurityException("You can only update your own questions");
        }

        question.setPrompt(request.getPrompt());
        question.setRubric(request.getRubric());
        question.setTimeLimitMinutes(request.getTimeLimitMinutes());
        question.setMaxScore(request.getMaxScore());

        question = questionRepo.save(question);
        log.info("Teacher {} updated essay question {}", teacherId, id);

        return mapToResponse(question);
    }

    @Override
    public EssayQuestionResponse getQuestionById(Long id) {
        EssayQuestion question = questionRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Question not found"));
        return mapToResponse(question);
    }

    @Override
    public PageResponse<EssayQuestionResponse> getAllActiveQuestions(Pageable pageable) {
        Page<EssayQuestion> questions = questionRepo.findByStatus(QuestionStatus.ACTIVE, pageable);
        Page<EssayQuestionResponse> responsePage = questions.map(this::mapToResponse);
        return new PageResponse<>(responsePage);
    }

    @Override
    public PageResponse<EssayQuestionResponse> getQuestionsByTeacher(Long teacherId, Pageable pageable) {
        Page<EssayQuestion> questions = questionRepo.findByCreatedByUserIdOrderByCreatedAtDesc(teacherId, pageable);
        Page<EssayQuestionResponse> responsePage = questions.map(this::mapToResponse);
        return new PageResponse<>(responsePage);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long id, Long teacherId) {
        EssayQuestion question = questionRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Question not found"));

        if (!question.getCreatedBy().getUserId().equals(teacherId)) {
            throw new SecurityException("You can only delete your own questions");
        }

        questionRepo.delete(question);
        log.info("Teacher {} deleted essay question {}", teacherId, id);
    }

    @Override
    @Transactional
    public void archiveQuestion(Long id, Long teacherId) {
        EssayQuestion question = questionRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Question not found"));

        if (!question.getCreatedBy().getUserId().equals(teacherId)) {
            throw new SecurityException("You can only archive your own questions");
        }

        question.setStatus(QuestionStatus.ARCHIVED);
        questionRepo.save(question);
        log.info("Teacher {} archived essay question {}", teacherId, id);
    }

    private EssayQuestionResponse mapToResponse(EssayQuestion question) {
        return EssayQuestionResponse.builder()
            .id(question.getId())
            .prompt(question.getPrompt())
            .rubric(question.getRubric())
            .timeLimitMinutes(question.getTimeLimitMinutes())
            .maxScore(question.getMaxScore())
            .status(question.getStatus())
            .createdBy(question.getCreatedBy().getUsername())
            .createdAt(question.getCreatedAt())
            .updatedAt(question.getUpdatedAt())
            .build();
    }
}
