//package com.example.demologin.controller;
//
//import com.example.demologin.annotation.ApiResponse;
//import com.example.demologin.annotation.PageResponse;
//import com.example.demologin.annotation.SecuredEndpoint;
//import com.example.demologin.dto.request.exam_taking.FinishExamRequest;
//import com.example.demologin.dto.request.exam_taking.StartExamRequest;
//import com.example.demologin.dto.request.exam_taking.SubmitAnswerRequest;
//import com.example.demologin.service.ExamTakingService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/exams")
//@Tag(name = "Exam Taking", description = "APIs for students to take exams")
//@AllArgsConstructor
//@Slf4j
//public class ExamTakingController {
//
//    private final ExamTakingService examTakingService;
//
//    @GetMapping("/available")
//    @SecuredEndpoint("EXAM_VIEW_AVAILABLE")
//    @PageResponse
//    @ApiResponse(message = "Available exams retrieved successfully")
//    @Operation(summary = "Get available exams", description = "Retrieve all available published exams")
//    public Object getAvailableExams(@Parameter(description = "Pagination information") Pageable pageable) {
//        return examTakingService.getAvailableExams(pageable);
//    }
//
//    @GetMapping("/available/difficulty/{difficulty}")
//    @SecuredEndpoint("EXAM_VIEW_AVAILABLE")
//    @PageResponse
//    @ApiResponse(message = "Available exams by difficulty retrieved successfully")
//    @Operation(summary = "Get available exams by difficulty", description = "Retrieve available exams filtered by difficulty level")
//    public Object getAvailableExamsByDifficulty(
//            @Parameter(description = "Difficulty level (EASY, MEDIUM, HARD)") @PathVariable String difficulty,
//            @Parameter(description = "Pagination information") Pageable pageable) {
//        return examTakingService.getAvailableExamsByDifficulty(difficulty, pageable);
//    }
//
//    @GetMapping("/available/level/{levelId}")
//    @SecuredEndpoint("EXAM_VIEW_AVAILABLE")
//    @PageResponse
//    @ApiResponse(message = "Available exams by level retrieved successfully")
//    @Operation(summary = "Get available exams by level", description = "Retrieve available exams filtered by level")
//    public Object getAvailableExamsByLevel(
//            @Parameter(description = "Level ID") @PathVariable Long levelId,
//            @Parameter(description = "Pagination information") Pageable pageable) {
//        return examTakingService.getAvailableExamsByLevel(levelId, pageable);
//    }
//
//    @PostMapping("/start")
//    @SecuredEndpoint("EXAM_TAKE")
//    @ApiResponse(message = "Exam started successfully")
//    @Operation(summary = "Start an exam", description = "Start taking an exam")
//    public Object startExam(@Valid @RequestBody StartExamRequest request) {
//        return examTakingService.startExam(request);
//    }
//
//    @GetMapping("/attempts/{examAttemptId}/questions")
//    @SecuredEndpoint("EXAM_TAKE")
//    @ApiResponse(message = "Exam questions retrieved successfully")
//    @Operation(summary = "Get exam questions", description = "Retrieve questions for the current exam attempt")
//    public Object getExamQuestions(
//            @Parameter(description = "Exam attempt ID") @PathVariable Long examAttemptId) {
//        return examTakingService.getExamQuestions(examAttemptId);
//    }
//
//    @PostMapping("/attempts/{examAttemptId}/answers")
//    @SecuredEndpoint("EXAM_TAKE")
//    @ApiResponse(message = "Answer submitted successfully")
//    @Operation(summary = "Submit answer", description = "Submit an answer to a question")
//    public ResponseEntity<String> submitAnswer(
//            @Parameter(description = "Exam attempt ID") @PathVariable Long examAttemptId,
//            @Valid @RequestBody SubmitAnswerRequest request) {
//        log.debug("Received submit answer request: examAttemptId={}, questionId={}, optionId={}, essayAnswer={}",
//                  examAttemptId, request.getQuestionId(), request.getOptionId(),
//                  request.getEssayAnswer() != null ? "[essay answer provided]" : "[no essay answer]");
//
//        try {
//            examTakingService.submitAnswer(examAttemptId, request);
//            log.debug("Answer submitted successfully for examAttemptId={}, questionId={}",
//                      examAttemptId, request.getQuestionId());
//            return ResponseEntity.ok("Answer submitted successfully");
//        } catch (Exception e) {
//            log.error("Error submitting answer: examAttemptId={}, questionId={}, optionId={}, error={}",
//                      examAttemptId, request.getQuestionId(), request.getOptionId(), e.getMessage(), e);
//            throw e;
//        }
//    }
//
//    @GetMapping("/attempts/{examAttemptId}")
//    @SecuredEndpoint("EXAM_TAKE")
//    @ApiResponse(message = "Exam attempt details retrieved successfully")
//    @Operation(summary = "Get current exam attempt", description = "Get details of the current exam attempt")
//    public Object getCurrentExamAttempt(
//            @Parameter(description = "Exam attempt ID") @PathVariable Long examAttemptId) {
//        return examTakingService.getCurrentExamAttempt(examAttemptId);
//    }
//
//    @PostMapping("/finish")
//    @SecuredEndpoint("EXAM_TAKE")
//    @ApiResponse(message = "Exam finished successfully")
//    @Operation(summary = "Finish exam", description = "Finish the exam and calculate results")
//    public Object finishExam(@Valid @RequestBody FinishExamRequest request) {
//        return examTakingService.finishExam(request);
//    }
//
//    @GetMapping("/results/{examAttemptId}")
//    @SecuredEndpoint("EXAM_VIEW_RESULTS")
//    @ApiResponse(message = "Exam results retrieved successfully")
//    @Operation(summary = "Get exam results", description = "Get detailed results of a completed exam")
//    public Object getExamResult(
//            @Parameter(description = "Exam attempt ID") @PathVariable Long examAttemptId) {
//        return examTakingService.getExamResult(examAttemptId);
//    }
//
//    @GetMapping("/my-history")
//    @SecuredEndpoint("EXAM_VIEW_HISTORY")
//    @PageResponse
//    @ApiResponse(message = "Exam history retrieved successfully")
//    @Operation(summary = "Get my exam history", description = "Retrieve student's exam history")
//    public Object getMyExamHistory(@Parameter(description = "Pagination information") Pageable pageable) {
//        return examTakingService.getMyExamHistory(pageable);
//    }
//
//    @GetMapping("/in-progress")
//    @SecuredEndpoint("EXAM_TAKE")
//    @ApiResponse(message = "In-progress exams retrieved successfully")
//    @Operation(summary = "Get in-progress exams", description = "Retrieve student's in-progress exams")
//    public Object getInProgressExams() {
//        return examTakingService.getInProgressExams();
//    }
//
//    @GetMapping("/debug/check-data")
//    @SecuredEndpoint("EXAM_TAKE")
//    @ApiResponse(message = "Debug data retrieved successfully")
//    @Operation(summary = "Debug data check", description = "Check if exam attempts and questions exist")
//    public Object debugCheckData() {
//        return examTakingService.debugCheckData();
//    }
//
//    @PostMapping("/debug/cleanup-inconsistent-state")
//    @SecuredEndpoint("EXAM_TAKE")
//    @ApiResponse(message = "Inconsistent state cleaned up successfully")
//    @Operation(summary = "Cleanup inconsistent exam state", description = "Fix inconsistent exam attempt states")
//    public Object cleanupInconsistentState() {
//        return examTakingService.cleanupInconsistentState();
//    }
//}