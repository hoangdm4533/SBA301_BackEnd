package com.example.demologin.serviceImpl;

import com.example.demologin.dto.request.EssaySubmissionStartRequest;
import com.example.demologin.dto.request.EssaySubmissionSubmitRequest;
import com.example.demologin.dto.request.TeacherGradingRequest;
import com.example.demologin.dto.response.EssaySubmissionResponse;
import com.example.demologin.dto.response.PageResponse;
import com.example.demologin.entity.EssayQuestion;
import com.example.demologin.entity.EssaySubmission;
import com.example.demologin.entity.User;
import com.example.demologin.enums.ActivityType;
import com.example.demologin.enums.SubmissionStatus;
import com.example.demologin.repository.EssayQuestionRepository;
import com.example.demologin.repository.EssaySubmissionRepository;
import com.example.demologin.repository.UserRepository;
import com.example.demologin.service.EssaySubmissionService;
import com.example.demologin.service.SubscriptionService;
import com.example.demologin.service.UserActivityLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EssaySubmissionServiceImpl implements EssaySubmissionService {
    private final EssaySubmissionRepository submissionRepo;
    private final EssayQuestionRepository questionRepo;
    private final UserRepository userRepo;
    private final SubscriptionService subscriptionService;
    private final UserActivityLogService activityLogService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public EssaySubmissionResponse startEssay(EssaySubmissionStartRequest request, Long userId) {
        log.debug("User {} starting essay question {}", userId, request.getEssayQuestionId());

        // 1. Check premium subscription
        if (!subscriptionService.hasPremium(userId)) {
            throw new SecurityException("Essay feature is only available for premium users");
        }

        // 2. Get user and question
        User user = userRepo.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        EssayQuestion question = questionRepo.findById(request.getEssayQuestionId())
            .orElseThrow(() -> new EntityNotFoundException("Essay question not found"));

        // 3. Check if already started
        Optional<EssaySubmission> existing = submissionRepo.findByUserUserIdAndEssayQuestionId(
            userId, question.getId());

        if (existing.isPresent()) {
            EssaySubmission submission = existing.get();
            
            // If expired, allow restart
            if (submission.getStatus() == SubmissionStatus.EXPIRED) {
                log.info("User {} restarting expired submission {}", userId, submission.getId());
                submission.setStatus(SubmissionStatus.ONGOING);
                submission.setStartedAt(LocalDateTime.now());
                submission.setAnswer(null);
                submission.setImageUrls(null);
                submission = submissionRepo.save(submission);
                return mapToResponse(submission, question);
            }
            
            // If ongoing, return existing
            if (submission.getStatus() == SubmissionStatus.ONGOING) {
                log.info("User {} continuing existing submission {}", userId, submission.getId());
                return mapToResponse(submission, question);
            }
            
            // If already submitted/graded, can't restart
            throw new IllegalStateException("You have already submitted this essay");
        }

        // 4. Create new submission
        EssaySubmission submission = EssaySubmission.builder()
            .user(user)
            .essayQuestion(question)
            .status(SubmissionStatus.ONGOING)
            .startedAt(LocalDateTime.now())
            .build();

        submission = submissionRepo.save(submission);
        log.info("Created new submission {} for user {} on question {}", submission.getId(), userId, question.getId());

        return mapToResponse(submission, question);
    }

    @Override
    @Transactional
    public EssaySubmissionResponse submitEssay(EssaySubmissionSubmitRequest request, Long userId) {
        log.debug("User {} submitting essay submission {}", userId, request.getSubmissionId());

        // 1. Validate submission
        EssaySubmission submission = submissionRepo.findById(request.getSubmissionId())
            .orElseThrow(() -> new EntityNotFoundException("Submission not found"));

        // 2. Check ownership
        if (!submission.getUser().getUserId().equals(userId)) {
            log.warn("User {} attempted to submit submission {} belonging to user {}", 
                userId, submission.getId(), submission.getUser().getUserId());
            throw new SecurityException("You can only submit your own submissions");
        }

        // 3. Check status
        if (submission.getStatus() != SubmissionStatus.ONGOING) {
            throw new IllegalStateException("Submission already submitted or expired");
        }

        EssayQuestion question = submission.getEssayQuestion();

        // 4. Check time limit
        long secondsElapsed = Duration.between(submission.getStartedAt(), LocalDateTime.now()).getSeconds();
        int timeLimitSeconds = question.getTimeLimitMinutes() * 60;

        if (secondsElapsed > timeLimitSeconds) {
            submission.setStatus(SubmissionStatus.EXPIRED);
            submissionRepo.save(submission);
            log.warn("Submission {} expired - time limit exceeded", submission.getId());
            throw new IllegalStateException("Time limit exceeded. Your submission has been marked as expired.");
        }

        // 5. Validate answer not empty
        if (request.getAnswer() == null || request.getAnswer().trim().isEmpty()) {
            throw new IllegalArgumentException("Answer cannot be empty");
        }

        // 6. Handle image URLs
        String imageUrlsJson = null;
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            try {
                imageUrlsJson = objectMapper.writeValueAsString(request.getImageUrls());
                log.debug("Saved {} image URLs for submission {}", request.getImageUrls().size(), submission.getId());
            } catch (Exception e) {
                log.error("Failed to serialize image URLs", e);
                throw new RuntimeException("Failed to process image URLs");
            }
        }

        // 7. Update submission
        submission.setAnswer(request.getAnswer());
        submission.setImageUrls(imageUrlsJson);
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setTimeSpentSeconds((int) secondsElapsed);
        submission.setStatus(SubmissionStatus.SUBMITTED);

        submission = submissionRepo.save(submission);
        log.info("Submission {} submitted successfully", submission.getId());

        // 8. Log activity
        User user = submission.getUser();
        activityLogService.logUserActivity(user, ActivityType.ESSAY_SUBMITTED, 
            "Submitted essay: " + question.getId() + " - Time spent: " + secondsElapsed + "s");

        return mapToResponse(submission, question);
    }

    @Override
    public EssaySubmissionResponse getMySubmission(Long submissionId, Long userId) {
        EssaySubmission submission = submissionRepo.findById(submissionId)
            .orElseThrow(() -> new EntityNotFoundException("Submission not found"));

        if (!submission.getUser().getUserId().equals(userId)) {
            throw new SecurityException("You can only view your own submissions");
        }

        return mapToResponse(submission, submission.getEssayQuestion());
    }

    @Override
    public PageResponse<EssaySubmissionResponse> getMySubmissions(Long userId, Pageable pageable) {
        Page<EssaySubmission> submissions = submissionRepo.findByUserUserIdOrderByStartedAtDesc(userId, pageable);
        Page<EssaySubmissionResponse> responsePage = submissions.map(s -> mapToResponse(s, s.getEssayQuestion()));
        return new PageResponse<>(responsePage);
    }

    @Override
    @Transactional
    public EssaySubmissionResponse gradeSubmission(TeacherGradingRequest request, Long teacherId) {
        log.debug("Teacher {} grading submission {}", teacherId, request.getSubmissionId());

        // 1. Get submission
        EssaySubmission submission = submissionRepo.findById(request.getSubmissionId())
            .orElseThrow(() -> new EntityNotFoundException("Submission not found"));

        // 2. Check if already graded
        if (submission.getStatus() == SubmissionStatus.GRADED) {
            log.warn("Submission {} already graded, updating score", submission.getId());
        }

        // 3. Validate score
        if (request.getScore() > submission.getEssayQuestion().getMaxScore()) {
            throw new IllegalArgumentException("Score cannot exceed max score: " + 
                submission.getEssayQuestion().getMaxScore());
        }

        // 4. Get teacher
        User teacher = userRepo.findById(teacherId)
            .orElseThrow(() -> new EntityNotFoundException("Teacher not found"));

        // 5. Update submission with grading
        submission.setScore(request.getScore());
        submission.setFeedback(request.getFeedback());
        submission.setDetailedFeedback(request.getDetailedFeedback());
        submission.setGradedBy(teacher);
        submission.setGradedAt(LocalDateTime.now());
        submission.setStatus(SubmissionStatus.GRADED);

        submission = submissionRepo.save(submission);
        log.info("Submission {} graded by teacher {} - Score: {}/{}", 
            submission.getId(), teacherId, request.getScore(), submission.getEssayQuestion().getMaxScore());

        // 6. Log activity for student
        User student = submission.getUser();
        activityLogService.logUserActivity(student, ActivityType.ESSAY_GRADED, 
            "Essay graded: " + submission.getEssayQuestion().getId() + 
            " - Score: " + request.getScore() + "/" + submission.getEssayQuestion().getMaxScore());

        return mapToResponse(submission, submission.getEssayQuestion());
    }

    @Override
    public PageResponse<EssaySubmissionResponse> getPendingSubmissions(Pageable pageable) {
        Page<EssaySubmission> submissions = submissionRepo.findByStatusOrderBySubmittedAtAsc(
            SubmissionStatus.SUBMITTED, pageable);
        Page<EssaySubmissionResponse> responsePage = submissions.map(s -> mapToResponse(s, s.getEssayQuestion()));
        return new PageResponse<>(responsePage);
    }

    @Override
    public PageResponse<EssaySubmissionResponse> getSubmissionsForQuestion(Long questionId, Pageable pageable) {
        Page<EssaySubmission> submissions = submissionRepo.findByEssayQuestionIdOrderBySubmittedAtDesc(
            questionId, pageable);
        Page<EssaySubmissionResponse> responsePage = submissions.map(s -> mapToResponse(s, s.getEssayQuestion()));
        return new PageResponse<>(responsePage);
    }

    @Override
    public EssaySubmissionResponse getSubmissionById(Long submissionId) {
        EssaySubmission submission = submissionRepo.findById(submissionId)
            .orElseThrow(() -> new EntityNotFoundException("Submission not found"));
        return mapToResponse(submission, submission.getEssayQuestion());
    }

    private boolean isExpired(EssaySubmission submission, EssayQuestion question) {
        if (submission.getStatus() != SubmissionStatus.ONGOING) {
            return false;
        }
        long elapsed = Duration.between(submission.getStartedAt(), LocalDateTime.now()).getSeconds();
        return elapsed > (question.getTimeLimitMinutes() * 60);
    }

    private EssaySubmissionResponse mapToResponse(EssaySubmission submission, EssayQuestion question) {
        Integer timeRemaining = null;
        Integer timeLimitSeconds = question.getTimeLimitMinutes() * 60;

        if (submission.getStatus() == SubmissionStatus.ONGOING) {
            long elapsed = Duration.between(submission.getStartedAt(), LocalDateTime.now()).getSeconds();
            timeRemaining = Math.max(0, timeLimitSeconds - (int) elapsed);
        }

        boolean isExpired = submission.getStatus() == SubmissionStatus.ONGOING && 
                           isExpired(submission, question);

        // Parse image URLs from JSON
        List<String> imageUrls = null;
        if (submission.getImageUrls() != null && !submission.getImageUrls().isEmpty()) {
            try {
                imageUrls = objectMapper.readValue(submission.getImageUrls(), 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
            } catch (Exception e) {
                log.error("Failed to parse image URLs for submission {}", submission.getId(), e);
            }
        }

        String gradedByName = submission.getGradedBy() != null ? submission.getGradedBy().getUsername() : null;

        return EssaySubmissionResponse.builder()
            .id(submission.getId())
            .essayQuestionId(question.getId())
            .prompt(question.getPrompt())
            .answer(submission.getAnswer())
            .imageUrls(imageUrls)
            .startedAt(submission.getStartedAt())
            .submittedAt(submission.getSubmittedAt())
            .timeSpentSeconds(submission.getTimeSpentSeconds())
            .timeRemainingSeconds(timeRemaining)
            .timeLimitSeconds(timeLimitSeconds)
            .score(submission.getScore())
            .maxScore(question.getMaxScore())
            .feedback(submission.getFeedback())
            .detailedFeedback(submission.getDetailedFeedback())
            .gradedBy(gradedByName)
            .status(submission.getStatus())
            .gradedAt(submission.getGradedAt())
            .isExpired(isExpired)
            .build();
    }
}
