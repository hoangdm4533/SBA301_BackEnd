package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.PageResponse;
import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.dto.request.exam.AddQuestionToExamRequest;
import com.example.demologin.dto.request.exam.ExamRequest;
import com.example.demologin.dto.response.ExamQuestionResponse;
import com.example.demologin.dto.response.ExamResponse;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.service.ExamService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exams")
@Tag(name = "Exam Management", description = "APIs for members to view and take exams")
@RequiredArgsConstructor
public class ExamAdminController {
    private final ExamService examService;

    @PostMapping
    @SecuredEndpoint("EXAM_CREATE")
    @ApiResponse(message = "Tạo exam thành công")
    public ResponseEntity<ResponseObject> create(@Valid @RequestBody ExamRequest request) {
        ExamResponse data = examService.createExam(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseObject(201, "Exam created successfully", data));
    }

    @GetMapping("/{id}")
    @SecuredEndpoint("EXAM_UPDATE") // hoặc quyền xem riêng nếu bạn có
    @ApiResponse(message = "Lấy thông tin exam thành công")
    public ResponseEntity<ResponseObject> get(@PathVariable Long id) {
        ExamResponse data = examService.getExamById(id);
        return ResponseEntity.ok(new ResponseObject(200, "Exam retrieved successfully", data));
    }

    @GetMapping
    @SecuredEndpoint("EXAM_UPDATE")
    @PageResponse
    public ResponseEntity<ResponseObject> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Page<ExamResponse> data = examService.getAllExams(page, size, sortBy, sortDir);
        return ResponseEntity.ok(new ResponseObject(200, "Exams retrieved successfully", data));
    }

    @PutMapping("/{id}")
    @SecuredEndpoint("EXAM_UPDATE")
    @ApiResponse(message = "Cập nhật exam thành công")
    public ResponseEntity<ResponseObject> update(@PathVariable Long id,
                                                 @Valid @RequestBody ExamRequest request) {
        ExamResponse data = examService.updateExam(id, request);
        return ResponseEntity.ok(new ResponseObject(200, "Exam updated successfully", data));
    }

    @DeleteMapping("/{id}")
    @SecuredEndpoint("EXAM_DELETE")
    @ApiResponse(message = "Xóa exam thành công")
    public ResponseEntity<ResponseObject> delete(@PathVariable Long id) {
        examService.deleteExam(id);
        return ResponseEntity.ok(new ResponseObject(200, "Exam deleted successfully", Map.of("id", id)));
    }

    @GetMapping("/exams?status=PUBLISHED")
    @SecuredEndpoint("EXAM_UPDATE")
    @PageResponse
    public ResponseEntity<ResponseObject> byStatus(@PathVariable String status,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(defaultValue = "id") String sortBy,
                                                   @RequestParam(defaultValue = "asc") String sortDir) {
        Page<ExamResponse> data = examService.getExamsByStatus(status, page, size, sortBy, sortDir);
        return ResponseEntity.ok(new ResponseObject(200, "Exams by status retrieved", data));
    }

    @GetMapping("/search")
    @SecuredEndpoint("EXAM_UPDATE")
    @PageResponse
    public ResponseEntity<ResponseObject> search(@RequestParam String keyword,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ExamResponse> data = examService.searchExams(keyword, pageable);
        return ResponseEntity.ok(new ResponseObject(200, "Exams search results", data));
    }

    @PostMapping("/{examId}/questions")
    @SecuredEndpoint("EXAM_QUESTION_ADD")
    @ApiResponse(message = "Thêm câu hỏi vào exam thành công")
    public ResponseEntity<ResponseObject> addQuestion(@PathVariable Long examId,
                                                      @Valid @RequestBody AddQuestionToExamRequest request) {
        ExamQuestionResponse data = examService.addQuestionToExam(examId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseObject(201, "Question added to exam", data));
    }

    @DeleteMapping("{examId}/questions")
    @SecuredEndpoint("EXAM_QUESTION_REMOVE")
    @ApiResponse(message = "Xóa câu hỏi khỏi exam thành công")
    public ResponseEntity<ResponseObject> removeQuestion(@PathVariable Long examId,
                                                         @PathVariable Long questionId) {
        examService.removeQuestionFromExam(examId, questionId);
        return ResponseEntity.ok(new ResponseObject(200, "Question removed from exam",
                Map.of("examId", examId, "questionId", questionId)));
    }

    @GetMapping("/{examId}/questions")
    @SecuredEndpoint("EXAM_UPDATE")
    @ApiResponse(message = "Lấy danh sách câu hỏi trong exam thành công")
    public ResponseEntity<ResponseObject> listQuestions(@PathVariable Long examId) {
        List<ExamQuestionResponse> data = examService.getQuestionsInExam(examId);
        return ResponseEntity.ok(new ResponseObject(200, "Exam questions retrieved", data));
    }

    @PutMapping("/{id}/publish")
    @SecuredEndpoint("EXAM_PUBLISH")
    @ApiResponse(message = "Publish exam thành công")
    public ResponseEntity<ResponseObject> publish(@PathVariable Long id) {
        examService.publishExam(id);
        return ResponseEntity.ok(new ResponseObject(200, "Exam published successfully", Map.of("id", id)));
    }

    @PutMapping("/{id}/archive")
    @SecuredEndpoint("EXAM_ARCHIVE")
    @ApiResponse(message = "Archive exam thành công")
    public ResponseEntity<ResponseObject> archive(@PathVariable Long id) {
        examService.archiveExam(id);
        return ResponseEntity.ok(new ResponseObject(200, "Exam archived successfully", Map.of("id", id)));
    }

    @GetMapping("/published")
    @SecuredEndpoint("EXAM_UPDATE")
    @PageResponse
    public ResponseEntity<ResponseObject> published(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        Page<ExamResponse> data = examService.getPublishedExams(PageRequest.of(page, size));
        return ResponseEntity.ok(new ResponseObject(200, "Published exams retrieved", data));
    }
}
