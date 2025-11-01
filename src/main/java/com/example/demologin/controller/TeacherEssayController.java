package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.AuthenticatedEndpoint;
import com.example.demologin.annotation.PageResponse;
import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.dto.request.EssayQuestionRequest;
import com.example.demologin.dto.request.TeacherGradingRequest;
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
@RequestMapping("/api/teacher/essays")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Teacher Essay", description = "APIs for teachers to create questions and grade essays")
public class TeacherEssayController {
    private final EssayQuestionService questionService;
    private final EssaySubmissionService submissionService;
    private final AccountUtils accountUtils;

    @GetMapping("/questions")
    @AuthenticatedEndpoint
    @PageResponse
    @ApiResponse(message = "Essay questions retrieved successfully")
    @Operation(summary = "Get all essay questions", 
               description = "Retrieve paginated list of all essay questions (teacher only)")
    public ResponseEntity<ResponseObject> getAllQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = Pageable.ofSize(size).withPage(page);
            var questions = questionService.getAllActiveQuestions(pageable);
            return ResponseEntity.ok(new ResponseObject(200, "Essay questions retrieved successfully", questions));
        } catch (Exception e) {
            log.error("Failed to get essay questions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseObject(500, e.getMessage(), null));
        }
    }

    @PostMapping("/questions")
    @AuthenticatedEndpoint
    @ApiResponse(message = "Essay question created successfully")
    @Operation(summary = "Create new essay question")
    public ResponseEntity<ResponseObject> createQuestion(@Valid @RequestBody EssayQuestionRequest request) {
        try {
            Long teacherId = accountUtils.getCurrentUser().getUserId();
            EssayQuestionResponse question = questionService.createQuestion(request, teacherId);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseObject(201, "Essay question created successfully", question));
        } catch (Exception e) {
            log.error("Failed to create essay question", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject(400, e.getMessage(), null));
        }
    }

    @PostMapping("/grade")
    @AuthenticatedEndpoint
    @ApiResponse(message = "Essay graded successfully")
    @Operation(summary = "Grade essay submission")
    public ResponseEntity<ResponseObject> gradeSubmission(@Valid @RequestBody TeacherGradingRequest request) {
        try {
            Long teacherId = accountUtils.getCurrentUser().getUserId();
            EssaySubmissionResponse submission = submissionService.gradeSubmission(request, teacherId);
            return ResponseEntity.ok(new ResponseObject(200, "Essay graded successfully", submission));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject(400, e.getMessage(), null));
        } catch (Exception e) {
            log.error("Failed to grade submission", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject(400, e.getMessage(), null));
        }
    }

    @GetMapping("/submissions/pending")
    @AuthenticatedEndpoint
    @ApiResponse(message = "Retrieved pending submissions")
    @Operation(summary = "Get pending submissions")
    public ResponseEntity<ResponseObject> getPendingSubmissions(Pageable pageable) {
        try {
            var submissions = submissionService.getPendingSubmissions(pageable);
            return ResponseEntity.ok(new ResponseObject(200, "Success", submissions));
        } catch (Exception e) {
            log.error("Failed to get pending submissions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseObject(500, e.getMessage(), null));
        }
    }

    @GetMapping("/submissions/{id}")
    @AuthenticatedEndpoint
    @ApiResponse(message = "Retrieved submission details")
    @Operation(summary = "Get submission details")
    public ResponseEntity<ResponseObject> getSubmissionDetails(@PathVariable Long id) {
        try {
            EssaySubmissionResponse submission = submissionService.getSubmissionById(id);
            return ResponseEntity.ok(new ResponseObject(200, "Success", submission));
        } catch (Exception e) {
            log.error("Failed to get submission {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseObject(404, e.getMessage(), null));
        }
    }
}
