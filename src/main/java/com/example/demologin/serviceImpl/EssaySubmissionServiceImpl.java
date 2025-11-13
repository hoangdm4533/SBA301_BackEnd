package com.example.demologin.serviceImpl;

import com.example.demologin.dto.request.TeacherGradingRequest;
import com.example.demologin.dto.request.essay.EssaySubmissionStartRequest;
import com.example.demologin.dto.response.PageResponse;
import com.example.demologin.dto.response.essay.EssaySubmissionResponse;
import com.example.demologin.dto.response.essay.SubmissionAttachmentResponse;
import com.example.demologin.entity.*;
import com.example.demologin.enums.SubmissionStatus;
import com.example.demologin.repository.*;
import com.example.demologin.service.CloudinaryService;
import com.example.demologin.service.EssaySubmissionService;
import com.example.demologin.service.SubscriptionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EssaySubmissionServiceImpl implements EssaySubmissionService {
    private final EssaySubmissionRepository submissionRepo;
    private final SubmissionAttachmentRepository attachmentRepo;
    private final EssayQuestionRepository questionRepo;
    private final UserRepository userRepo;
    private final CloudinaryService cloudinaryService;
    private final SubscriptionService subscriptionService;

    @Override
    @Transactional
    public EssaySubmissionResponse startEssay(EssaySubmissionStartRequest request) {
        // Check subscription
        Long userId =getCurrentUserId();
        if (!subscriptionService.hasPremium(userId)) {
            throw new SecurityException("Premium subscription required to start essay");
        }

        // Check if already started
        submissionRepo.findByUserUserIdAndEssayQuestionId(userId, request.getEssayQuestionId())
            .ifPresent(existing -> {
                if (existing.getStatus() == SubmissionStatus.ONGOING) {
                    throw new IllegalStateException("You have already started this essay");
                }
            });

        User user = userRepo.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));

        EssayQuestion question = questionRepo.findById(request.getEssayQuestionId())
            .orElseThrow(() -> new EntityNotFoundException("Essay question not found"));

        EssaySubmission submission = EssaySubmission.builder()
            .user(user)
            .essayQuestion(question)
            .startedAt(LocalDateTime.now())
            .status(SubmissionStatus.ONGOING)
            .build();

        submission = submissionRepo.save(submission);

        log.info("User {} started essay question {}", userId, request.getEssayQuestionId());
        return mapToResponse(submission);
    }

    @Override
    @Transactional
    public EssaySubmissionResponse submitEssayWithFiles(
            Long submissionId, 
            String answer, 
            MultipartFile[] imageFiles,
            MultipartFile[] documentFiles) {
        Long userId = getCurrentUserId();
        EssaySubmission submission = submissionRepo.findById(submissionId)
            .orElseThrow(() -> new EntityNotFoundException("Submission not found"));

        // Verify ownership
        if (!submission.getUser().getUserId().equals(userId)) {
            throw new SecurityException("You can only submit your own essays");
        }

        if (submission.getStatus() != SubmissionStatus.ONGOING) {
            throw new IllegalStateException("This submission has already been submitted");
        }

        // Check time limit
        EssayQuestion question = submission.getEssayQuestion();
        if (question.getTimeLimitMinutes() != null) {
            long minutesElapsed = ChronoUnit.MINUTES.between(submission.getStartedAt(), LocalDateTime.now());
            if (minutesElapsed > question.getTimeLimitMinutes()) {
                submission.setStatus(SubmissionStatus.EXPIRED);
                submissionRepo.save(submission);
                throw new IllegalStateException("Time limit exceeded");
            }
        }

        // Update submission text
        submission.setAnswer(answer);
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setTimeSpentSeconds((int) ChronoUnit.SECONDS.between(submission.getStartedAt(), LocalDateTime.now()));

        try {
            // Upload image files
            if (imageFiles != null && imageFiles.length > 0) {
                List<Map<String, Object>> imageResults = cloudinaryService.uploadMultipleImages(
                    imageFiles, "submission_images"
                );
                
                for (Map<String, Object> result : imageResults) {
                    SubmissionAttachment attachment = SubmissionAttachment.builder()
                        .fileName((String) result.get("file_name"))
                        .originalFileName((String) result.get("original_file_name"))
                        .fileUrl((String) result.get("file_url"))
                        .cloudinaryPublicId((String) result.get("cloudinary_public_id"))
                        .fileType((String) result.get("file_type"))
                        .fileSize((Long) result.get("file_size"))
                        .attachmentType("IMAGE")
                        .build();
                    
                    submission.addAttachment(attachment);
                }
                log.info("Uploaded {} images for submission {}", imageResults.size(), submissionId);
            }

            // Upload document files
            if (documentFiles != null && documentFiles.length > 0) {
                List<Map<String, Object>> docResults = cloudinaryService.uploadMultipleDocuments(
                    documentFiles, "submission_documents"
                );
                
                for (Map<String, Object> result : docResults) {
                    SubmissionAttachment attachment = SubmissionAttachment.builder()
                        .fileName((String) result.get("file_name"))
                        .originalFileName((String) result.get("original_file_name"))
                        .fileUrl((String) result.get("file_url"))
                        .cloudinaryPublicId((String) result.get("cloudinary_public_id"))
                        .fileType((String) result.get("file_type"))
                        .fileSize((Long) result.get("file_size"))
                        .attachmentType("DOCUMENT")
                        .build();
                    
                    submission.addAttachment(attachment);
                }
                log.info("Uploaded {} documents for submission {}", docResults.size(), submissionId);
            }

        } catch (IOException e) {
            log.error("Error uploading files for submission", e);
            throw new RuntimeException("Failed to upload files: " + e.getMessage());
        }

        submission.setStatus(SubmissionStatus.PENDING_GRADING);
        submission = submissionRepo.save(submission);

        log.info("User {} submitted essay with {} attachments", userId, submission.getAttachments().size());
        return mapToResponse(submission);
    }

    @Override
    public EssaySubmissionResponse getMySubmission(Long submissionId) {
        Long userId = getCurrentUserId();
        EssaySubmission submission = submissionRepo.findById(submissionId)
            .orElseThrow(() -> new EntityNotFoundException("Submission not found"));

        if (!submission.getUser().getUserId().equals(userId)) {
            throw new SecurityException("You can only view your own submissions");
        }
        return mapToResponse(submission);
    }

    @Override
    public PageResponse<EssaySubmissionResponse> getMySubmissions( Pageable pageable) {
        Long userId = getCurrentUserId();
        Page<EssaySubmission> submissions = submissionRepo.findByUserUserIdOrderByStartedAtDesc(userId, pageable);
        Page<EssaySubmissionResponse> responsePage = submissions.map(this::mapToResponse);
        return new PageResponse<>(responsePage);
    }

    @Override
    @Transactional
    public EssaySubmissionResponse gradeSubmission(TeacherGradingRequest request) {
        Long teacherId = getCurrentUserId();
        EssaySubmission submission = submissionRepo.findById(request.getSubmissionId())
            .orElseThrow(() -> new EntityNotFoundException("Submission not found"));

        if (submission.getStatus() != SubmissionStatus.PENDING_GRADING) {
            throw new IllegalStateException("Only pending submissions can be graded");
        }

        User teacher = userRepo.findById(teacherId)
            .orElseThrow(() -> new EntityNotFoundException("Teacher not found"));

        submission.setScore(request.getScore());
        submission.setFeedback(request.getFeedback());
        submission.setDetailedFeedback(request.getDetailedFeedback());
        submission.setGradedBy(teacher);
        submission.setGradedAt(LocalDateTime.now());
        submission.setStatus(SubmissionStatus.GRADED);

        submission = submissionRepo.save(submission);

        log.info("Teacher {} graded submission {} with score {}", teacherId, submission.getId(), request.getScore());
        return mapToResponse(submission);
    }

    @Override
    public PageResponse<EssaySubmissionResponse> getPendingSubmissions(Pageable pageable) {
        // Get all pending submissions (for admin)
        Page<EssaySubmission> submissions = submissionRepo.findByStatusOrderBySubmittedAtAsc(
            SubmissionStatus.PENDING_GRADING, pageable
        );
        Page<EssaySubmissionResponse> responsePage = submissions.map(this::mapToResponse);
        return new PageResponse<>(responsePage);
    }

    @Override
    public PageResponse<EssaySubmissionResponse> getPendingSubmissionsForTeacher(Long teacherId, Pageable pageable) {
        // Get pending submissions only for questions created by this teacher
        Page<EssaySubmission> submissions = submissionRepo
            .findByStatusAndEssayQuestionCreatedByUserIdOrderBySubmittedAtAsc(
                SubmissionStatus.PENDING_GRADING, teacherId, pageable
            );
        Page<EssaySubmissionResponse> responsePage = submissions.map(this::mapToResponse);
        return new PageResponse<>(responsePage);
    }

    @Override
    public PageResponse<EssaySubmissionResponse> getSubmissionsForQuestion(Long questionId, Pageable pageable) {
        Page<EssaySubmission> submissions = submissionRepo.findByEssayQuestionIdOrderBySubmittedAtDesc(
            questionId, pageable
        );
        Page<EssaySubmissionResponse> responsePage = submissions.map(this::mapToResponse);
        return new PageResponse<>(responsePage);
    }

    @Override
    public EssaySubmissionResponse getSubmissionById(Long submissionId) {
        EssaySubmission submission = submissionRepo.findById(submissionId)
            .orElseThrow(() -> new EntityNotFoundException("Submission not found"));
        return mapToResponse(submission);
    }

    private EssaySubmissionResponse mapToResponse(EssaySubmission submission) {
        EssayQuestion question = submission.getEssayQuestion();
        
        // Calculate time remaining
        Integer timeRemaining = null;
        Boolean isExpired = false;
        
        if (question.getTimeLimitMinutes() != null && submission.getStatus() == SubmissionStatus.ONGOING) {
            long minutesElapsed = ChronoUnit.MINUTES.between(submission.getStartedAt(), LocalDateTime.now());
            long secondsRemaining = (question.getTimeLimitMinutes() * 60) - (minutesElapsed * 60);
            
            if (secondsRemaining <= 0) {
                isExpired = true;
                timeRemaining = 0;
            } else {
                timeRemaining = (int) secondsRemaining;
            }
        }

        // Get attachments
        List<SubmissionAttachment> attachments = attachmentRepo.findBySubmissionId(submission.getId());
        List<SubmissionAttachmentResponse> attachmentResponses = attachments.stream()
            .map(this::mapToAttachmentResponse)
            .collect(Collectors.toList());

        return EssaySubmissionResponse.builder()
            .id(submission.getId        ())
            .studentId(submission.getUser().getUserId())
            .studentName(submission.getUser().getUsername())
            .essayQuestionId(question.getId())
            .prompt(question.getPrompt())
            .answer(submission.getAnswer())
            .attachments(attachmentResponses)
            .startedAt(submission.getStartedAt())
            .submittedAt(submission.getSubmittedAt())
            .timeSpentSeconds(submission.getTimeSpentSeconds())
            .timeRemainingSeconds(timeRemaining)
            .timeLimitSeconds(question.getTimeLimitMinutes() != null ? question.getTimeLimitMinutes() * 60 : null)
            .score(submission.getScore())
            .maxScore(question.getMaxScore())
            .feedback(submission.getFeedback())
            .detailedFeedback(submission.getDetailedFeedback())
            .gradedBy(submission.getGradedBy() != null ? submission.getGradedBy().getUsername() : null)
            .status(submission.getStatus())
            .gradedAt(submission.getGradedAt())
            .isExpired(isExpired)
            .build();
    }

    private SubmissionAttachmentResponse mapToAttachmentResponse(SubmissionAttachment attachment) {
        String downloadUrl = "/api/essay-submissions/attachments/" + attachment.getId() + "/download";
        
        return SubmissionAttachmentResponse.builder()
            .id(attachment.getId())
            .fileName(attachment.getFileName())
            .originalFileName(attachment.getOriginalFileName())
            .fileUrl(attachment.getFileUrl())
            .downloadUrl(downloadUrl)
            .fileType(attachment.getFileType())
            .fileSize(attachment.getFileSize())
            .attachmentType(attachment.getAttachmentType())
            .uploadedAt(attachment.getUploadedAt())
            .build();
    }
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User not authenticated");
        }
        User user = (User) authentication.getPrincipal();
        return user.getUserId();
    }
}
