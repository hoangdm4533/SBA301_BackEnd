package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.AuthenticatedEndpoint;
import com.example.demologin.annotation.PageResponse;
import com.example.demologin.dto.request.EssaySubmissionStartRequest;
import com.example.demologin.dto.request.EssaySubmissionSubmitRequest;
import com.example.demologin.dto.response.EssayQuestionResponse;
import com.example.demologin.dto.response.EssaySubmissionResponse;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.service.EssayQuestionService;
import com.example.demologin.service.EssaySubmissionService;
import com.example.demologin.utils.AccountUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/essays")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Student Essay", description = "APIs for students to do essays (Premium feature)")
public class StudentEssayController {
    private final EssayQuestionService questionService;
    private final EssaySubmissionService submissionService;
    private final AccountUtils accountUtils;

    @GetMapping("/questions")
    @AuthenticatedEndpoint
    @PageResponse
    @Operation(summary = "Get all active essay questions", description = "Premium students can view available essays")
    public ResponseEntity<ResponseObject> getActiveQuestions(Pageable pageable) {
        try {
            var questions = questionService.getAllActiveQuestions(pageable);
            return ResponseEntity.ok(new ResponseObject(200, "Success", questions));
        } catch (Exception e) {
            log.error("Failed to get essay questions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseObject(500, e.getMessage(), null));
        }
    }

    @GetMapping("/questions/{id}")
    @AuthenticatedEndpoint
    @ApiResponse(message = "Retrieved essay question details")
    @Operation(summary = "Get essay question by ID")
    public ResponseEntity<ResponseObject> getQuestionById(@PathVariable Long id) {
        try {
            EssayQuestionResponse question = questionService.getQuestionById(id);
            return ResponseEntity.ok(new ResponseObject(200, "Success", question));
        } catch (Exception e) {
            log.error("Failed to get essay question {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseObject(404, e.getMessage(), null));
        }
    }

    @PostMapping("/start")
    @AuthenticatedEndpoint
    @ApiResponse(message = "Essay started successfully")
    @Operation(
        summary = "Start an essay",
        description = "Start a new essay submission. Premium users only. Timer starts immediately."
    )
    public ResponseEntity<ResponseObject> startEssay(@Valid @RequestBody EssaySubmissionStartRequest request) {
        try {
            Long userId = accountUtils.getCurrentUser().getUserId();
            EssaySubmissionResponse submission = submissionService.startEssay(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseObject(201, "Essay started successfully", submission));
        } catch (SecurityException e) {
            log.warn("Non-premium user attempted to start essay: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ResponseObject(403, e.getMessage(), null));
        } catch (Exception e) {
            log.error("Failed to start essay", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject(400, e.getMessage(), null));
        }
    }

    @PostMapping("/submit")
    @AuthenticatedEndpoint
    @ApiResponse(message = "Essay submitted successfully")
    @Operation(
        summary = "Submit essay answer",
        description = "Submit your essay answer with optional images. Must be within time limit."
    )
    public ResponseEntity<ResponseObject> submitEssay(@Valid @RequestBody EssaySubmissionSubmitRequest request) {
        try {
            Long userId = accountUtils.getCurrentUser().getUserId();
            EssaySubmissionResponse submission = submissionService.submitEssay(request, userId);
            return ResponseEntity.ok(new ResponseObject(200, "Essay submitted successfully. Waiting for teacher grading.", submission));
        } catch (IllegalStateException e) {
            log.warn("Invalid essay submission attempt: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject(400, e.getMessage(), null));
        } catch (Exception e) {
            log.error("Failed to submit essay", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject(400, e.getMessage(), null));
        }
    }

    @GetMapping("/submissions")
    @AuthenticatedEndpoint
    @PageResponse
    @Operation(summary = "Get my essay submissions", description = "View all your essay submissions with status and scores")
    public ResponseEntity<ResponseObject> getMySubmissions(Pageable pageable) {
        try {
            Long userId = accountUtils.getCurrentUser().getUserId();
            var submissions = submissionService.getMySubmissions(userId, pageable);
            return ResponseEntity.ok(new ResponseObject(200, "Success", submissions));
        } catch (Exception e) {
            log.error("Failed to get submissions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseObject(500, e.getMessage(), null));
        }
    }

    @GetMapping("/submissions/{id}")
    @AuthenticatedEndpoint
    @ApiResponse(message = "Retrieved submission details")
    @Operation(summary = "Get submission by ID", description = "View details of a specific submission including teacher feedback")
    public ResponseEntity<ResponseObject> getSubmissionById(@PathVariable Long id) {
        try {
            Long userId = accountUtils.getCurrentUser().getUserId();
            EssaySubmissionResponse submission = submissionService.getMySubmission(id, userId);
            return ResponseEntity.ok(new ResponseObject(200, "Success", submission));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ResponseObject(403, e.getMessage(), null));
        } catch (Exception e) {
            log.error("Failed to get submission {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseObject(404, e.getMessage(), null));
        }
    }
}
