package com.example.demologin.controller;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demologin.dto.request.essay.EssayQuestionRequest;
import com.example.demologin.dto.response.ApiResponse;
import com.example.demologin.dto.response.PageResponse;
import com.example.demologin.dto.response.essay.EssayAttachmentResponse;
import com.example.demologin.dto.response.essay.EssayQuestionResponse;
import com.example.demologin.service.EssayQuestionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/essay-questions")
@RequiredArgsConstructor
@Slf4j
public class EssayQuestionController {
    private final EssayQuestionService essayQuestionService;
    private final ObjectMapper objectMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EssayQuestionResponse>> createQuestion(
            @RequestPart("data") String requestData,
            @RequestPart(value = "documents", required = false) MultipartFile[] documents,
            @RequestPart(value = "images", required = false) MultipartFile[] images) {
        
        try {
            EssayQuestionRequest request = objectMapper.readValue(requestData, EssayQuestionRequest.class);
            EssayQuestionResponse response = essayQuestionService.createQuestion(request, documents, images);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Essay question created successfully"));
        } catch (Exception e) {
            log.error("Error creating essay question", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to create essay question: " + e.getMessage()));
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EssayQuestionResponse>> updateQuestion(
            @PathVariable Long id,
            @RequestPart("data") String requestData,
            @RequestPart(value = "documents", required = false) MultipartFile[] documents,
            @RequestPart(value = "images", required = false) MultipartFile[] images) {
        
        try {
            EssayQuestionRequest request = objectMapper.readValue(requestData, EssayQuestionRequest.class);
            EssayQuestionResponse response = essayQuestionService.updateQuestion(id, request, documents, images);
            
            return ResponseEntity.ok(ApiResponse.success(response, "Essay question updated successfully"));
        } catch (Exception e) {
            log.error("Error updating essay question", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to update essay question: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<EssayQuestionResponse>> getQuestionById(@PathVariable Long id) {
        try {
            EssayQuestionResponse response = essayQuestionService.getQuestionById(id);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("Error retrieving essay question", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Question not found: " + e.getMessage()));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<PageResponse<EssayQuestionResponse>>> getAllActiveQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        
        try {
            Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC") ? 
                Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            PageResponse<EssayQuestionResponse> response = 
                essayQuestionService.getAllActiveQuestions(pageable);
            
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("Error retrieving essay questions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve questions: " + e.getMessage()));
        }
    }

    @GetMapping("/my-questions")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<PageResponse<EssayQuestionResponse>>> getMyQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            PageResponse<EssayQuestionResponse> response = 
                essayQuestionService.getQuestionsByTeacher(pageable);
            
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("Error retrieving teacher questions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve questions: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN') or hasRole('MEMBER')")
    public ResponseEntity<ApiResponse<PageResponse<EssayQuestionResponse>>> searchQuestions(
            @RequestParam(required = false) Long gradeId,
            @RequestParam(required = false) Long chapterId,
            @RequestParam(required = false) Long lessonId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            PageResponse<EssayQuestionResponse> response = 
                essayQuestionService.searchActiveQuestions(gradeId, chapterId, lessonId, pageable);
            
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("Error searching essay questions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to search questions: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> changeQuestionStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        
        try {
            essayQuestionService.changeQuestionStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success(null, "Question status updated successfully"));
        } catch (Exception e) {
            log.error("Error changing question status", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Failed to change status: " + e.getMessage()));
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<EssayQuestionResponse>>> getAllQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        
        try {
            Sort.Direction direction = sortDirection.equalsIgnoreCase("ASC") ? 
                Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            PageResponse<EssayQuestionResponse> response = 
                essayQuestionService.getAllQuestions(pageable);
            
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            log.error("Error retrieving all essay questions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to retrieve questions: " + e.getMessage()));
        }
    }

    @GetMapping("/attachments/{attachmentId}/download")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN') or hasRole('STUDENT ')")
    public ResponseEntity<?> downloadAttachment(@PathVariable Long attachmentId) {
        try {
            EssayAttachmentResponse attachment = essayQuestionService.getAttachmentById(attachmentId);
            
            log.info("Download request for attachment ID: {}", attachmentId);
            log.info("Original filename: {}", attachment.getOriginalFileName());
            log.info("Cloudinary URL: {}", attachment.getFileUrl());
            
            // Download file from Cloudinary and stream to client with proper filename
            URL url = new URL(attachment.getFileUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            
            // Get input stream from Cloudinary
            InputStream inputStream = connection.getInputStream();
            
            // Encode filename for Content-Disposition header (handles Vietnamese characters)
            String encodedFilename = URLEncoder.encode(attachment.getOriginalFileName(), StandardCharsets.UTF_8)
                    .replace("+", "%20");
            
            // Set headers for download with original filename
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=\"" + attachment.getOriginalFileName() + 
                "\"; filename*=UTF-8''" + encodedFilename);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            
            log.info("Streaming file: {} ({})", attachment.getOriginalFileName(), attachment.getFileType());
            
            // Stream the file to client
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(inputStream));
                    
        } catch (Exception e) {
            log.error("Error downloading attachment", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Attachment not found: " + e.getMessage()));
        }
    }
}
