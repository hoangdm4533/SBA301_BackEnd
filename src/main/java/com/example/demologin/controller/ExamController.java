//package com.example.demologin.controller;
//
//import com.example.demologin.annotation.ApiResponse;
//import com.example.demologin.annotation.PageResponse;
//import com.example.demologin.annotation.SecuredEndpoint;
//import com.example.demologin.dto.request.exam.ExamRequest;
//import com.example.demologin.dto.request.exam.AddQuestionToExamRequest;
//import com.example.demologin.dto.response.ExamResponse;
//import com.example.demologin.dto.response.ExamQuestionResponse;
//import com.example.demologin.service.ExamService;
//import lombok.AllArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import jakarta.validation.Valid;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/exams")
//@AllArgsConstructor
//public class ExamController {
//
//    private final ExamService examService;
//
//    @PostMapping
//    @SecuredEndpoint("EXAM_CREATE")
//    @ApiResponse(message = "Tạo exam thành công")
//    public ResponseEntity<ExamResponse> createExam(@Valid @RequestBody ExamRequest request) {
//        ExamResponse response = examService.createExam(request);
//        return new ResponseEntity<>(response, HttpStatus.CREATED);
//    }
//
//    @GetMapping("/{id}")
//    @ApiResponse(message = "Lấy thông tin exam thành công")
//    public ResponseEntity<ExamResponse> getExamById(@PathVariable Long id) {
//        ExamResponse response = examService.getExamById(id);
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping
//    @PageResponse
//    public ResponseEntity<Page<ExamResponse>> getAllExams(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "id") String sortBy,
//            @RequestParam(defaultValue = "asc") String sortDir) {
//        Page<ExamResponse> response = examService.getAllExams(page, size, sortBy, sortDir);
//        return ResponseEntity.ok(response);
//    }
//
//    @PutMapping("/{id}")
//    @SecuredEndpoint("EXAM_UPDATE")
//    @ApiResponse(message = "Cập nhật exam thành công")
//    public ResponseEntity<ExamResponse> updateExam(
//            @PathVariable Long id,
//            @Valid @RequestBody ExamRequest request) {
//        ExamResponse response = examService.updateExam(id, request);
//        return ResponseEntity.ok(response);
//    }
//
//    @DeleteMapping("/{id}")
//    @SecuredEndpoint("EXAM_DELETE")
//    @ApiResponse(message = "Xóa exam thành công")
//    public ResponseEntity<Void> deleteExam(@PathVariable Long id) {
//        examService.deleteExam(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    @GetMapping("/status/{status}")
//    @PageResponse
//    public ResponseEntity<Page<ExamResponse>> getExamsByStatus(
//            @PathVariable String status,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "id") String sortBy,
//            @RequestParam(defaultValue = "asc") String sortDir) {
//        Page<ExamResponse> response = examService.getExamsByStatus(status, page, size, sortBy, sortDir);
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/search")
//    @PageResponse
//    public ResponseEntity<Page<ExamResponse>> searchExams(
//            @RequestParam String keyword,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "id") String sortBy,
//            @RequestParam(defaultValue = "asc") String sortDir) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<ExamResponse> response = examService.searchExams(keyword, pageable);
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/{examId}/questions")
//    @SecuredEndpoint("EXAM_QUESTION_ADD")
//    @ApiResponse(message = "Thêm câu hỏi vào exam thành công")
//    public ResponseEntity<ExamQuestionResponse> addQuestionToExam(
//            @PathVariable Long examId,
//            @Valid @RequestBody AddQuestionToExamRequest request) {
//        ExamQuestionResponse response = examService.addQuestionToExam(examId, request);
//        return new ResponseEntity<>(response, HttpStatus.CREATED);
//    }
//
//    @DeleteMapping("/{examId}/questions/{questionId}")
//    @SecuredEndpoint("EXAM_QUESTION_REMOVE")
//    @ApiResponse(message = "Xóa câu hỏi khỏi exam thành công")
//    public ResponseEntity<Void> removeQuestionFromExam(
//            @PathVariable Long examId,
//            @PathVariable Long questionId) {
//        examService.removeQuestionFromExam(examId, questionId);
//        return ResponseEntity.noContent().build();
//    }
//
//    @GetMapping("/{examId}/questions")
//    @ApiResponse(message = "Lấy danh sách câu hỏi trong exam thành công")
//    public ResponseEntity<List<ExamQuestionResponse>> getQuestionsInExam(@PathVariable Long examId) {
//        List<ExamQuestionResponse> response = examService.getQuestionsInExam(examId);
//        return ResponseEntity.ok(response);
//    }
//
//    @PutMapping("/{id}/publish")
//    @SecuredEndpoint("EXAM_PUBLISH")
//    @ApiResponse(message = "Publish exam thành công")
//    public ResponseEntity<Void> publishExam(@PathVariable Long id) {
//        examService.publishExam(id);
//        return ResponseEntity.ok().build();
//    }
//
//    @PutMapping("/{id}/archive")
//    @SecuredEndpoint("EXAM_ARCHIVE")
//    @ApiResponse(message = "Archive exam thành công")
//    public ResponseEntity<Void> archiveExam(@PathVariable Long id) {
//        examService.archiveExam(id);
//        return ResponseEntity.ok().build();
//    }
//
//    @GetMapping("/published")
//    @PageResponse
//    public ResponseEntity<Page<ExamResponse>> getPublishedExams(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "id") String sortBy,
//            @RequestParam(defaultValue = "asc") String sortDir) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<ExamResponse> response = examService.getPublishedExams(pageable);
//        return ResponseEntity.ok(response);
//    }
//}