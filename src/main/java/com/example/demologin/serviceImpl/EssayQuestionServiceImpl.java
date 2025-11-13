package com.example.demologin.serviceImpl;

import com.example.demologin.dto.request.essay.EssayQuestionRequest;
import com.example.demologin.dto.response.essay.EssayAttachmentResponse;
import com.example.demologin.dto.response.essay.EssayQuestionResponse;
import com.example.demologin.dto.response.PageResponse;
import com.example.demologin.entity.*;
import com.example.demologin.enums.QuestionStatus;
import com.example.demologin.repository.*;
import com.example.demologin.service.CloudinaryService;
import com.example.demologin.service.EssayQuestionService;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EssayQuestionServiceImpl implements EssayQuestionService {
    private final EssayQuestionRepository questionRepo;
    private final EssayAttachmentRepository attachmentRepo;
    private final UserRepository userRepo;
    private final GradeRepository gradeRepo;
    private final ChapterRepository chapterRepo;
    private final LessonRepository lessonRepo;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional
    public EssayQuestionResponse createQuestion(EssayQuestionRequest request, MultipartFile[] documentFiles, MultipartFile[] imageFiles) {
        Long teacherId = getCurrentUserId();
        
        User teacher = userRepo.findById(teacherId)
            .orElseThrow(() -> new EntityNotFoundException("Teacher not found"));

        EssayQuestion question = EssayQuestion.builder()
            .grade(findGradeById(request.getGradeId()))
            .chapter(findChapterById(request.getChapterId()))
            .lesson(findLessonById(request.getLessonId()))
            .prompt(request.getPrompt())
            .rubric(request.getRubric())
            .timeLimitMinutes(request.getTimeLimitMinutes())
            .maxScore(request.getMaxScore())
            .status(QuestionStatus.ACTIVE)
            .createdBy(teacher)
            .build();

        question = questionRepo.save(question);

        try {
            int totalUploaded = 0;
            
            // Handle document uploads
            if (documentFiles != null && documentFiles.length > 0) {
                List<Map<String, Object>> uploadResults = cloudinaryService.uploadMultipleDocuments(documentFiles, "essay_questions");
                
                for (Map<String, Object> uploadResult : uploadResults) {
                    EssayAttachment attachment = EssayAttachment.builder()
                        .fileName((String) uploadResult.get("file_name"))
                        .originalFileName((String) uploadResult.get("original_file_name"))
                        .fileUrl((String) uploadResult.get("file_url"))
                        .cloudinaryPublicId((String) uploadResult.get("cloudinary_public_id"))
                        .fileType((String) uploadResult.get("file_type"))
                        .fileSize((Long) uploadResult.get("file_size"))
                        .build();
                    
                    question.addAttachment(attachment);
                }
                totalUploaded += uploadResults.size();
                log.info("Uploaded {} documents for question", uploadResults.size());
            }
            
            // Handle image uploads
            if (imageFiles != null && imageFiles.length > 0) {
                List<Map<String, Object>> uploadResults = cloudinaryService.uploadMultipleImages(imageFiles, "essay_questions_images");
                
                for (Map<String, Object> uploadResult : uploadResults) {
                    EssayAttachment attachment = EssayAttachment.builder()
                        .fileName((String) uploadResult.get("file_name"))
                        .originalFileName((String) uploadResult.get("original_file_name"))
                        .fileUrl((String) uploadResult.get("file_url"))
                        .cloudinaryPublicId((String) uploadResult.get("cloudinary_public_id"))
                        .fileType((String) uploadResult.get("file_type"))
                        .fileSize((Long) uploadResult.get("file_size"))
                        .build();
                    
                    question.addAttachment(attachment);
                }
                totalUploaded += uploadResults.size();
                log.info("Uploaded {} images for question", uploadResults.size());
            }
            
            if (totalUploaded > 0) {
                log.info("Teacher {} created essay question {} with {} total attachments", 
                    teacherId, question.getId(), totalUploaded);
                questionRepo.save(question);
            } else {
                log.info("Teacher {} created essay question {} without attachments", teacherId, question.getId());
            }
            
        } catch (IOException e) {
            log.error("Error uploading files for essay question", e);
            throw new RuntimeException("Failed to upload files: " + e.getMessage());
        }

        return mapToResponse(question);
    }

    @Override
    @Transactional
    public EssayQuestionResponse updateQuestion(Long id, EssayQuestionRequest request, MultipartFile[] documentFiles, MultipartFile[] imageFiles) {
        Long teacherId = getCurrentUserId();
        
        EssayQuestion question = questionRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Question not found"));

        // Check ownership
        if (!question.getCreatedBy().getUserId().equals(teacherId)) {
            throw new SecurityException("You can only update your own questions");
        }

        question.setGrade(findGradeById(request.getGradeId()));
        question.setChapter(findChapterById(request.getChapterId()));
        question.setLesson(findLessonById(request.getLessonId()));
        question.setPrompt(request.getPrompt());
        question.setRubric(request.getRubric());
        question.setTimeLimitMinutes(request.getTimeLimitMinutes());
        question.setMaxScore(request.getMaxScore());

        question = questionRepo.save(question);

        // Handle new file uploads - only if files provided
        boolean hasFiles = (documentFiles != null && documentFiles.length > 0) || 
                          (imageFiles != null && imageFiles.length > 0);
        
        if (hasFiles) {
            try {
                // Delete old attachments from Cloudinary
                List<EssayAttachment> oldAttachments = attachmentRepo.findByEssayQuestionId(id);
                for (EssayAttachment oldAttachment : oldAttachments) {
                    try {
                        // Check file type to use correct delete method
                        String fileType = oldAttachment.getFileType().toLowerCase();
                        if (fileType.matches("jpg|jpeg|png|gif|webp|bmp|svg")) {
                            cloudinaryService.deleteImage(oldAttachment.getCloudinaryPublicId());
                        } else {
                            cloudinaryService.deleteDocument(oldAttachment.getCloudinaryPublicId());
                        }
                    } catch (IOException e) {
                        log.warn("Failed to delete old file from Cloudinary: {}", oldAttachment.getCloudinaryPublicId());
                    }
                }
                
                // Clear old attachments (this will also delete from DB due to orphanRemoval = true)
                question.clearAttachments();
                
                int totalUploaded = 0;
                
                // Upload new documents
                if (documentFiles != null && documentFiles.length > 0) {
                    List<Map<String, Object>> uploadResults = cloudinaryService.uploadMultipleDocuments(documentFiles, "essay_questions");
                    
                    for (Map<String, Object> uploadResult : uploadResults) {
                        EssayAttachment attachment = EssayAttachment.builder()
                            .fileName((String) uploadResult.get("file_name"))
                            .originalFileName((String) uploadResult.get("original_file_name"))
                            .fileUrl((String) uploadResult.get("file_url"))
                            .cloudinaryPublicId((String) uploadResult.get("cloudinary_public_id"))
                            .fileType((String) uploadResult.get("file_type"))
                            .fileSize((Long) uploadResult.get("file_size"))
                            .build();
                        
                        question.addAttachment(attachment);
                    }
                    totalUploaded += uploadResults.size();
                    log.info("Uploaded {} documents for question update", uploadResults.size());
                }
                
                // Upload new images
                if (imageFiles != null && imageFiles.length > 0) {
                    List<Map<String, Object>> uploadResults = cloudinaryService.uploadMultipleImages(imageFiles, "essay_questions_images");
                    
                    for (Map<String, Object> uploadResult : uploadResults) {
                        EssayAttachment attachment = EssayAttachment.builder()
                            .fileName((String) uploadResult.get("file_name"))
                            .originalFileName((String) uploadResult.get("original_file_name"))
                            .fileUrl((String) uploadResult.get("file_url"))
                            .cloudinaryPublicId((String) uploadResult.get("cloudinary_public_id"))
                            .fileType((String) uploadResult.get("file_type"))
                            .fileSize((Long) uploadResult.get("file_size"))
                            .build();
                        
                        question.addAttachment(attachment);
                    }
                    totalUploaded += uploadResults.size();
                    log.info("Uploaded {} images for question update", uploadResults.size());
                }
                
                log.info("Teacher {} updated essay question {} with {} new attachments", 
                    teacherId, id, totalUploaded);
                
                // Save the question with new attachments
                questionRepo.save(question);
            } catch (IOException e) {
                log.error("Error uploading files for essay question update", e);
                throw new RuntimeException("Failed to upload files: " + e.getMessage());
            }
        }

        return mapToResponse(question);
    }

    @Override
    public EssayQuestionResponse getQuestionById(Long id) {
        EssayQuestion question = questionRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Question not found"));
        return mapToResponse(question);
    }

    @Override
    public EssayAttachmentResponse getAttachmentById(Long attachmentId) {
        EssayAttachment attachment = attachmentRepo.findById(attachmentId)
            .orElseThrow(() -> new EntityNotFoundException("Attachment not found with id: " + attachmentId));
        return mapToAttachmentResponse(attachment);
    }

    @Override
    public PageResponse<EssayQuestionResponse> getAllActiveQuestions(Pageable pageable) {
        Page<EssayQuestion> questions = questionRepo.findByStatus(QuestionStatus.ACTIVE, pageable);
        Page<EssayQuestionResponse> responsePage = questions.map(this::mapToResponse);
        return new PageResponse<>(responsePage);
    }

    @Override
    public PageResponse<EssayQuestionResponse> getQuestionsByTeacher(Pageable pageable) {
        Long teacherId = getCurrentUserId();
        Page<EssayQuestion> questions = questionRepo.findByCreatedByUserIdOrderByCreatedAtDesc(teacherId, pageable);
        Page<EssayQuestionResponse> responsePage = questions.map(this::mapToResponse);
        return new PageResponse<>(responsePage);
    }

    @Override
    public PageResponse<EssayQuestionResponse> getAllQuestions(Pageable pageable) {
        Page<EssayQuestion> questions = questionRepo.findAll(pageable);
        Page<EssayQuestionResponse> responsePage = questions.map(this::mapToResponse);
        return new PageResponse<>(responsePage);
    }

    @Override
    @Transactional
    public void changeQuestionStatus(Long id, String status) {
        Long teacherId = getCurrentUserId();
        EssayQuestion question = questionRepo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Question not found"));

        if (!question.getCreatedBy().getUserId().equals(teacherId)) {
            throw new SecurityException("You can only change status of your own questions");
        }

        QuestionStatus newStatus;
        try {
            newStatus = QuestionStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status. Allowed values: ACTIVE, INACTIVE, ARCHIVED");
        }

        question.setStatus(newStatus);
        questionRepo.save(question);
        log.info("Teacher {} changed essay question {} status to {}", teacherId, id, newStatus);
    }

    @Override
    public PageResponse<EssayQuestionResponse> searchActiveQuestions(
            Long gradeId, Long chapterId, Long lessonId, Pageable pageable) {
        
        Page<EssayQuestion> questionPage;
        
        // Search with different combinations of filters
        if (gradeId != null && chapterId != null && lessonId != null) {
            questionPage = questionRepo.findByStatusAndGradeIdAndChapterIdAndLessonId(
                QuestionStatus.ACTIVE, gradeId, chapterId, lessonId, pageable);
        } else if (gradeId != null && chapterId != null) {
            questionPage = questionRepo.findByStatusAndGradeIdAndChapterId(
                QuestionStatus.ACTIVE, gradeId, chapterId, pageable);
        } else if (gradeId != null) {
            questionPage = questionRepo.findByStatusAndGradeId(
                QuestionStatus.ACTIVE, gradeId, pageable);
        } else {
            questionPage = questionRepo.findByStatus(QuestionStatus.ACTIVE, pageable);
        }
        
        List<EssayQuestionResponse> responses = questionPage.getContent().stream()
                .map(this::mapToResponse)
                .toList();
                
        return new PageResponse<>(
                responses,
                questionPage.getNumber(),
                questionPage.getSize(),
                questionPage.getTotalElements(),
                questionPage.getTotalPages(),
                questionPage.isLast()
        );
    }

    // Helper methods
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User not authenticated");
        }
        User user = (User) authentication.getPrincipal();
        return user.getUserId();
    }

    private Grade findGradeById(Long gradeId) {
        return gradeRepo.findById(gradeId)
            .orElseThrow(() -> new EntityNotFoundException("Grade not found with id: " + gradeId));
    }

    private Chapter findChapterById(Long chapterId) {
        return chapterRepo.findById(chapterId)
            .orElseThrow(() -> new EntityNotFoundException("Chapter not found with id: " + chapterId));
    }

    private Lesson findLessonById(Long lessonId) {
        return lessonRepo.findById(lessonId)
            .orElseThrow(() -> new EntityNotFoundException("Lesson not found with id: " + lessonId));
    }

    private EssayQuestionResponse mapToResponse(EssayQuestion question) {
        // Get attachments
        List<EssayAttachment> attachments = attachmentRepo.findByEssayQuestionId(question.getId());
        
        List<EssayAttachmentResponse> attachmentResponses = attachments.stream()
            .map(this::mapToAttachmentResponse)
            .collect(Collectors.toList());
        
        List<String> attachmentUrls = attachments.stream()
            .map(EssayAttachment::getFileUrl)
            .collect(Collectors.toList());

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
            .attachments(attachmentResponses)
            .attachmentUrls(attachmentUrls)
            .build();
    }

    private EssayAttachmentResponse mapToAttachmentResponse(EssayAttachment attachment) {
        // Generate download URL for this attachment
        String downloadUrl = "/api/essay-questions/attachments/" + attachment.getId() + "/download";
        
        return EssayAttachmentResponse.builder()
            .id(attachment.getId())
            .fileName(attachment.getFileName())
            .originalFileName(attachment.getOriginalFileName())
            .fileUrl(attachment.getFileUrl())
            .downloadUrl(downloadUrl)
            .fileType(attachment.getFileType())
            .fileSize(attachment.getFileSize())
            .uploadedAt(attachment.getUploadedAt())
            .build();
    }
}
