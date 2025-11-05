package com.example.demologin.serviceImpl;

import com.example.demologin.dto.request.EssayQuestionRequest;
import com.example.demologin.dto.response.EssayQuestionResponse;
import com.example.demologin.dto.response.PageResponse;
import com.example.demologin.entity.*;
import com.example.demologin.enums.QuestionStatus;
import com.example.demologin.repository.*;
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
    private final GradeRepository gradeRepo;
    private final ChapterRepository chapterRepo;
    private final LessonRepository lessonRepo;

    @Override
    @Transactional
    public EssayQuestionResponse createQuestion(EssayQuestionRequest request, Long teacherId) {
        User teacher = userRepo.findById(teacherId)
            .orElseThrow(() -> new EntityNotFoundException("Teacher not found"));

        // Validate Grade, Chapter, Lesson
        Grade grade = gradeRepo.findById(request.getGradeId())
            .orElseThrow(() -> new EntityNotFoundException("Grade not found with ID: " + request.getGradeId()));

        Chapter chapter = chapterRepo.findById(request.getChapterId())
            .orElseThrow(() -> new EntityNotFoundException("Chapter not found with ID: " + request.getChapterId()));

        Lesson lesson = lessonRepo.findById(request.getLessonId())
            .orElseThrow(() -> new EntityNotFoundException("Lesson not found with ID: " + request.getLessonId()));

        // Validate hierarchy: Chapter belongs to Grade, Lesson belongs to Chapter
        if (!chapter.getGrade().getId().equals(grade.getId())) {
            throw new IllegalArgumentException("Chapter does not belong to the specified Grade");
        }

        if (!lesson.getChapter().getId().equals(chapter.getId())) {
            throw new IllegalArgumentException("Lesson does not belong to the specified Chapter");
        }

        EssayQuestion question = EssayQuestion.builder()
            .grade(grade)
            .chapter(chapter)
            .lesson(lesson)
            .prompt(request.getPrompt())
            .rubric(request.getRubric())
            .timeLimitMinutes(request.getTimeLimitMinutes())
            .maxScore(request.getMaxScore())
            .status(QuestionStatus.ACTIVE)
            .createdBy(teacher)
            .build();

        question = questionRepo.save(question);
        log.info("Teacher {} created essay question {} for Grade {}, Chapter {}, Lesson {}", 
            teacherId, question.getId(), grade.getId(), chapter.getId(), lesson.getId());

        return mapToResponse(question);
    }

    @Override
    @Transactional
    public EssayQuestionResponse updateQuestion(Long questionId, EssayQuestionRequest request, Long teacherId) {
        EssayQuestion question = questionRepo.findById(questionId)
            .orElseThrow(() -> new EntityNotFoundException("Essay question not found"));

        if (!question.getCreatedBy().getUserId().equals(teacherId)) {
            throw new IllegalArgumentException("You can only update your own questions");
        }

        // Validate Grade, Chapter, Lesson
        Grade grade = gradeRepo.findById(request.getGradeId())
            .orElseThrow(() -> new EntityNotFoundException("Grade not found with ID: " + request.getGradeId()));

        Chapter chapter = chapterRepo.findById(request.getChapterId())
            .orElseThrow(() -> new EntityNotFoundException("Chapter not found with ID: " + request.getChapterId()));

        Lesson lesson = lessonRepo.findById(request.getLessonId())
            .orElseThrow(() -> new EntityNotFoundException("Lesson not found with ID: " + request.getLessonId()));

        // Validate hierarchy: Chapter belongs to Grade, Lesson belongs to Chapter
        if (!chapter.getGrade().getId().equals(grade.getId())) {
            throw new IllegalArgumentException("Chapter does not belong to the specified Grade");
        }

        if (!lesson.getChapter().getId().equals(chapter.getId())) {
            throw new IllegalArgumentException("Lesson does not belong to the specified Chapter");
        }

        question.setGrade(grade);
        question.setChapter(chapter);
        question.setLesson(lesson);
        question.setPrompt(request.getPrompt());
        question.setRubric(request.getRubric());
        question.setTimeLimitMinutes(request.getTimeLimitMinutes());
        question.setMaxScore(request.getMaxScore());

        question = questionRepo.save(question);
        log.info("Teacher {} updated essay question {} to Grade {}, Chapter {}, Lesson {}", 
            teacherId, questionId, grade.getId(), chapter.getId(), lesson.getId());

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
            .gradeId(question.getGrade().getId())
            .gradeNumber(question.getGrade().getGradeNumber())
            .chapterId(question.getChapter().getId())
            .chapterName(question.getChapter().getName())
            .lessonId(question.getLesson().getId())
            .lessonName(question.getLesson().getLessonName())
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
