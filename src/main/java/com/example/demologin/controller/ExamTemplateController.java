package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.PageResponse;
import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.dto.request.exam_template.ExamTemplateRequest;
import com.example.demologin.dto.request.exam_template.AddQuestionToExamRequest;
import com.example.demologin.dto.response.ExamTemplateResponse;
import com.example.demologin.dto.response.ExamQuestionResponse;
import com.example.demologin.service.ExamTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam-templates")
@Tag(name = "Exam Template Management", description = "APIs for managing exam templates and questions")
@AllArgsConstructor
public class ExamTemplateController {
    
    private final ExamTemplateService examTemplateService;

    @PostMapping
    @SecuredEndpoint("EXAM_TEMPLATE_CREATE")
    @ApiResponse(message = "Exam template created successfully")
    @Operation(summary = "Create exam template", description = "Create a new exam template")
    public Object createExamTemplate(@Valid @RequestBody ExamTemplateRequest request) {
        return examTemplateService.createExamTemplate(request);
    }

    @GetMapping("/{id}")
    @SecuredEndpoint("EXAM_TEMPLATE_VIEW")
    @ApiResponse(message = "Exam template retrieved successfully")
    @Operation(summary = "Get exam template by ID", description = "Retrieve an exam template by its ID")
    public Object getExamTemplateById(
            @Parameter(description = "Exam template ID") @PathVariable Long id) {
        return examTemplateService.getExamTemplateById(id);
    }

    @GetMapping
    @SecuredEndpoint("EXAM_TEMPLATE_VIEW")
    @PageResponse
    @ApiResponse(message = "Exam templates retrieved successfully")
    @Operation(summary = "Get all exam templates", description = "Retrieve all exam templates with pagination")
    public Object getAllExamTemplates(
            @Parameter(description = "Pagination information") Pageable pageable) {
        return examTemplateService.getAllExamTemplates(pageable);
    }

    @GetMapping("/level/{levelId}")
    @SecuredEndpoint("EXAM_TEMPLATE_VIEW")
    @PageResponse
    @ApiResponse(message = "Exam templates by level retrieved successfully")
    @Operation(summary = "Get exam templates by level", description = "Retrieve exam templates filtered by level")
    public Object getExamTemplatesByLevel(
            @Parameter(description = "Level ID") @PathVariable Long levelId,
            @Parameter(description = "Pagination information") Pageable pageable) {
        return examTemplateService.getExamTemplatesByLevel(levelId, pageable);
    }

    @GetMapping("/status/{status}")
    @SecuredEndpoint("EXAM_TEMPLATE_VIEW")
    @PageResponse
    @ApiResponse(message = "Exam templates by status retrieved successfully")
    @Operation(summary = "Get exam templates by status", description = "Retrieve exam templates filtered by status")
    public Object getExamTemplatesByStatus(
            @Parameter(description = "Status (DRAFT, PUBLISHED, ARCHIVED)") @PathVariable String status,
            @Parameter(description = "Pagination information") Pageable pageable) {
        return examTemplateService.getExamTemplatesByStatus(status, pageable);
    }

    @GetMapping("/search")
    @SecuredEndpoint("EXAM_TEMPLATE_VIEW")
    @PageResponse
    @ApiResponse(message = "Exam templates search completed successfully")
    @Operation(summary = "Search exam templates", description = "Search exam templates by keyword")
    public Object searchExamTemplates(
            @Parameter(description = "Search keyword") @RequestParam String keyword,
            @Parameter(description = "Pagination information") Pageable pageable) {
        return examTemplateService.searchExamTemplates(keyword, pageable);
    }

    @GetMapping("/search/level/{levelId}")
    @SecuredEndpoint("EXAM_TEMPLATE_VIEW")
    @PageResponse
    @ApiResponse(message = "Exam templates search by level completed successfully")
    @Operation(summary = "Search exam templates by level", description = "Search exam templates by keyword within a specific level")
    public Object searchExamTemplatesByLevel(
            @Parameter(description = "Level ID") @PathVariable Long levelId,
            @Parameter(description = "Search keyword") @RequestParam String keyword,
            @Parameter(description = "Pagination information") Pageable pageable) {
        return examTemplateService.searchExamTemplatesByLevel(levelId, keyword, pageable);
    }

    @PutMapping("/{id}")
    @SecuredEndpoint("EXAM_TEMPLATE_UPDATE")
    @ApiResponse(message = "Exam template updated successfully")
    @Operation(summary = "Update exam template", description = "Update an existing exam template")
    public Object updateExamTemplate(
            @Parameter(description = "Exam template ID") @PathVariable Long id,
            @Valid @RequestBody ExamTemplateRequest request) {
        return examTemplateService.updateExamTemplate(id, request);
    }

    @DeleteMapping("/{id}")
    @SecuredEndpoint("EXAM_TEMPLATE_DELETE")
    @Operation(summary = "Delete exam template", description = "Delete an exam template")
    public ResponseEntity<String> deleteExamTemplate(
            @Parameter(description = "Exam template ID") @PathVariable Long id) {
        examTemplateService.deleteExamTemplate(id);
        return ResponseEntity.ok("Exam template deleted successfully");
    }

    // Question management endpoints
    @PostMapping("/{examTemplateId}/questions")
    @SecuredEndpoint("EXAM_TEMPLATE_MANAGE_QUESTIONS")
    @ApiResponse(message = "Question added to exam template successfully")
    @Operation(summary = "Add question to exam", description = "Add a question to an exam template")
    public Object addQuestionToExam(
            @Parameter(description = "Exam template ID") @PathVariable Long examTemplateId,
            @Valid @RequestBody AddQuestionToExamRequest request) {
        return examTemplateService.addQuestionToExam(examTemplateId, request);
    }

    @DeleteMapping("/{examTemplateId}/questions/{questionId}")
    @SecuredEndpoint("EXAM_TEMPLATE_MANAGE_QUESTIONS")
    @Operation(summary = "Remove question from exam", description = "Remove a question from an exam template")
    public ResponseEntity<String> removeQuestionFromExam(
            @Parameter(description = "Exam template ID") @PathVariable Long examTemplateId,
            @Parameter(description = "Question ID") @PathVariable Long questionId) {
        examTemplateService.removeQuestionFromExam(examTemplateId, questionId);
        return ResponseEntity.ok("Question removed from exam template successfully");
    }

    @PutMapping("/{examTemplateId}/questions/{questionId}")
    @SecuredEndpoint("EXAM_TEMPLATE_MANAGE_QUESTIONS")
    @ApiResponse(message = "Question in exam template updated successfully")
    @Operation(summary = "Update question in exam", description = "Update question details in an exam template")
    public Object updateQuestionInExam(
            @Parameter(description = "Exam template ID") @PathVariable Long examTemplateId,
            @Parameter(description = "Question ID") @PathVariable Long questionId,
            @Valid @RequestBody AddQuestionToExamRequest request) {
        examTemplateService.updateQuestionInExam(examTemplateId, questionId, request);
        // Service method is void, so we return the updated questions list
        return examTemplateService.getQuestionsInExam(examTemplateId);
    }

    @GetMapping("/{examTemplateId}/questions")
    @SecuredEndpoint("EXAM_TEMPLATE_VIEW")
    @ApiResponse(message = "Questions in exam template retrieved successfully")
    @Operation(summary = "Get questions in exam", description = "Retrieve all questions in an exam template")
    public Object getQuestionsInExam(
            @Parameter(description = "Exam template ID") @PathVariable Long examTemplateId) {
        return examTemplateService.getQuestionsInExam(examTemplateId);
    }

    @PutMapping("/{examTemplateId}/questions/reorder")
    @SecuredEndpoint("EXAM_TEMPLATE_MANAGE_QUESTIONS")
    @ApiResponse(message = "Questions reordered successfully")
    @Operation(summary = "Reorder questions", description = "Reorder questions in an exam template")
    public Object reorderQuestionsInExam(
            @Parameter(description = "Exam template ID") @PathVariable Long examTemplateId,
            @RequestBody List<Long> questionIds) {
        examTemplateService.reorderQuestionsInExam(examTemplateId, questionIds);
        // Service method is void, so we return the reordered questions list
        return examTemplateService.getQuestionsInExam(examTemplateId);
    }

    // Status management endpoints
    @PatchMapping("/{id}/publish")
    @SecuredEndpoint("EXAM_TEMPLATE_PUBLISH")
    @ApiResponse(message = "Exam template published successfully")
    @Operation(summary = "Publish exam template", description = "Publish an exam template")
    public Object publishExamTemplate(
            @Parameter(description = "Exam template ID") @PathVariable Long id) {
        examTemplateService.publishExamTemplate(id);
        return examTemplateService.getExamTemplateById(id);
    }

    @PatchMapping("/{id}/archive")
    @SecuredEndpoint("EXAM_TEMPLATE_UPDATE")
    @ApiResponse(message = "Exam template archived successfully")
    @Operation(summary = "Archive exam template", description = "Archive an exam template")
    public Object archiveExamTemplate(
            @Parameter(description = "Exam template ID") @PathVariable Long id) {
        examTemplateService.archiveExamTemplate(id);
        return examTemplateService.getExamTemplateById(id);
    }

    @PatchMapping("/{id}/approve")
    @SecuredEndpoint("EXAM_TEMPLATE_APPROVE")
    @ApiResponse(message = "Exam template approved successfully")
    @Operation(summary = "Approve exam template", description = "Approve an exam template")
    public Object approveExamTemplate(
            @Parameter(description = "Exam template ID") @PathVariable Long id) {
        examTemplateService.approveExamTemplate(id);
        return examTemplateService.getExamTemplateById(id);
    }
}