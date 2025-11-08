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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exams")
@Tag(name = "Exam Management", description = "APIs for admin and teacher to manage exams")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
public class ExamManagementController {

    private final ExamService examService;

    @PostMapping
    @ApiResponse(message = "Tạo exam thành công")
    public ResponseEntity<ResponseObject> create(@Valid @RequestBody ExamRequest request) {
        ExamResponse data = examService.createExam(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseObject(HttpStatus.CREATED.value(), "Exam created successfully", data));
    }

    @GetMapping("/{id}")
    @ApiResponse(message = "Lấy thông tin exam thành công")
    public ResponseEntity<ResponseObject> get(@PathVariable Long id) {
        ExamResponse data = examService.getExamById(id);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Exam retrieved successfully", data));
    }

    @GetMapping
    @PageResponse
    public ResponseEntity<ResponseObject> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Page<ExamResponse> data = examService.getAllExams(page, size, sortBy, sortDir);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Exams retrieved successfully", data));
    }

    @PutMapping("/{id}")
    @ApiResponse(message = "Cập nhật exam thành công")
    public ResponseEntity<ResponseObject> update(@PathVariable Long id,
                                                 @Valid @RequestBody ExamRequest request) {
        ExamResponse data = examService.updateExam(id, request);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Exam updated successfully", data));
    }

    @DeleteMapping("/{id}")
    @ApiResponse(message = "Xóa exam thành công")
    public ResponseEntity<ResponseObject> delete(@PathVariable Long id) {
        examService.deleteExam(id);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Exam deleted successfully", Map.of("id", id)));
    }

    // Lọc theo trạng thái (ví dụ: DRAFT/PUBLISHED/ARCHIVED)
    @GetMapping("/by-status")
    @PageResponse
    public ResponseEntity<ResponseObject> byStatus(@RequestParam String status,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(defaultValue = "id") String sortBy,
                                                   @RequestParam(defaultValue = "asc") String sortDir) {
        Page<ExamResponse> data = examService.getExamsByStatus(status, page, size, sortBy, sortDir);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Exams by status retrieved", data));
    }

    @GetMapping("/search")
    @PageResponse
    public ResponseEntity<ResponseObject> search(@RequestParam String keyword,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ExamResponse> data = examService.searchExams(keyword, pageable);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Exams search results", data));
    }

    @PostMapping("/{examId}/questions")
    @ApiResponse(message = "Thêm câu hỏi vào exam thành công")
    public ResponseEntity<ResponseObject> addQuestion(@PathVariable Long examId,
                                                      @Valid @RequestBody AddQuestionToExamRequest request) {
        ExamQuestionResponse data = examService.addQuestionToExam(examId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseObject(HttpStatus.CREATED.value(), "Question added to exam", data));
    }

    @DeleteMapping("/{examId}/questions/{questionId}")
    @ApiResponse(message = "Xóa câu hỏi khỏi exam thành công")
    public ResponseEntity<ResponseObject> removeQuestion(@PathVariable Long examId,
                                                         @PathVariable Long questionId) {
        examService.removeQuestionFromExam(examId, questionId);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Question removed from exam",
                Map.of("examId", examId, "questionId", questionId)
        ));
    }

    @GetMapping("/{examId}/questions")
    @ApiResponse(message = "Lấy danh sách câu hỏi trong exam thành công")
    public ResponseEntity<ResponseObject> listQuestions(@PathVariable Long examId) {
        List<ExamQuestionResponse> data = examService.getQuestionsInExam(examId);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Exam questions retrieved", data));
    }

    @PutMapping("/{id}/publish")
    @ApiResponse(message = "Publish exam thành công")
    public ResponseEntity<ResponseObject> publish(@PathVariable Long id) {
        examService.publishExam(id);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Exam published successfully", Map.of("id", id)));
    }

    @PutMapping("/{id}/archive")
    @ApiResponse(message = "Archive exam thành công")
    public ResponseEntity<ResponseObject> archive(@PathVariable Long id) {
        examService.archiveExam(id);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Exam archived successfully", Map.of("id", id)));
    }

    @GetMapping("/published")
    @PageResponse
    public ResponseEntity<ResponseObject> published(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        Page<ExamResponse> data = examService.getPublishedExams(PageRequest.of(page, size));
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Published exams retrieved", data));
    }
}
