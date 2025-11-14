package com.example.demologin.controller;

import com.example.demologin.dto.request.TeacherGradingRequest;
import com.example.demologin.dto.request.essay.EssaySubmissionStartRequest;
import com.example.demologin.dto.response.ApiResponse;
import com.example.demologin.dto.response.PageResponse;
import com.example.demologin.dto.response.essay.EssaySubmissionResponse;
import com.example.demologin.entity.User;
import com.example.demologin.repository.SubmissionAttachmentRepository;
import com.example.demologin.service.EssaySubmissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/essay-submissions")
@RequiredArgsConstructor
@Slf4j
public class EssaySubmissionController {
    private final EssaySubmissionService submissionService;
    private final SubmissionAttachmentRepository attachmentRepo;
    private final ObjectMapper objectMapper;

    @PostMapping("/start")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<EssaySubmissionResponse>> startEssay(
            @RequestBody EssaySubmissionStartRequest request) {
        try {
            EssaySubmissionResponse response = submissionService.startEssay(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Essay started successfully"));
        } catch (SecurityException e) {
            log.error("Security error starting essay", e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error starting essay", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to start essay: " + e.getMessage()));
        }
    }

    @PostMapping(value = "/{submissionId}/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<EssaySubmissionResponse>> submitEssay(
            @PathVariable Long submissionId,
            @RequestPart("data") String answerData,
            @RequestPart(value = "images", required = false) MultipartFile[] images,
            @RequestPart(value = "documents", required = false) MultipartFile[] documents) {
        try {
            // Parse answer from JSON string (similar to create essay question)
            String answer = answerData;
            
            EssaySubmissionResponse response = submissionService.submitEssayWithFiles(
                submissionId, answer, images, documents
            );
            return ResponseEntity.ok(ApiResponse.success(response, "Essay submitted successfully"));
        } catch (IllegalStateException e) {
            log.error("State error submitting essay", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error submitting essay", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to submit essay: " + e.getMessage()));
        }
    }

    @GetMapping("/{submissionId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EssaySubmissionResponse>> getSubmission(@PathVariable Long submissionId) {
        try {
            EssaySubmissionResponse response = submissionService.getMySubmission(submissionId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("Error retrieving submission", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Submission not found: " + e.getMessage()));
        }
    }

    @GetMapping("/my-submissions")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<PageResponse<EssaySubmissionResponse>>> getMySubmissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            PageResponse<EssaySubmissionResponse> response = submissionService.getMySubmissions(pageable);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("Error retrieving submissions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve submissions: " + e.getMessage()));
        }
    }

    @PostMapping("/grade")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EssaySubmissionResponse>> gradeSubmission(
            @RequestBody TeacherGradingRequest request) {
        try {
            EssaySubmissionResponse response = submissionService.gradeSubmission(request);
            return ResponseEntity.ok(ApiResponse.success(response, "Submission graded successfully"));
        } catch (Exception e) {
            log.error("Error grading submission", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to grade submission: " + e.getMessage()));
        }
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<EssaySubmissionResponse>>> getPendingSubmissions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long userId = getCurrentUserId();
            Pageable pageable = PageRequest.of(page, size);
            
            // Check if user is teacher - get only their questions' submissions
            // Admin gets all pending submissions
            PageResponse<EssaySubmissionResponse> response;
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
            if (isAdmin) {
                response = submissionService.getPendingSubmissions(pageable);
            } else {
                response = submissionService.getPendingSubmissionsForTeacher(userId, pageable);
            }
            
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("Error retrieving pending submissions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve submissions: " + e.getMessage()));
        }
    }

    @GetMapping("/question/{questionId}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<EssaySubmissionResponse>>> getSubmissionsForQuestion(
            @PathVariable Long questionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            PageResponse<EssaySubmissionResponse> response = 
                submissionService.getSubmissionsForQuestion(questionId, pageable);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("Error retrieving submissions for question", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve submissions: " + e.getMessage()));
        }
    }

    @GetMapping("/attachments/{attachmentId}/download")
    @PreAuthorize("hasRole('STUDENT') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<?> downloadAttachment(@PathVariable Long attachmentId) {
        try {
            var attachment = attachmentRepo.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));
            
            log.info("Download request for attachment ID: {}", attachmentId);
            log.info("Original filename: {}", attachment.getOriginalFileName());
            log.info("Cloudinary URL: {}", attachment.getFileUrl());
            
            // Download file from Cloudinary and stream to client with proper filename
            URL url = new URL(attachment.getFileUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            
            InputStream inputStream = connection.getInputStream();
            
            String encodedFilename = URLEncoder.encode(attachment.getOriginalFileName(), StandardCharsets.UTF_8)
                    .replace("+", "%20");
            
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=\"" + attachment.getOriginalFileName() + 
                "\"; filename*=UTF-8''" + encodedFilename);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            
            log.info("Streaming file: {} ({})", attachment.getOriginalFileName(), attachment.getFileType());
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new org.springframework.core.io.InputStreamResource(inputStream));
                    
        } catch (Exception e) {
            log.error("Error downloading attachment", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Attachment not found: " + e.getMessage()));
        }
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
