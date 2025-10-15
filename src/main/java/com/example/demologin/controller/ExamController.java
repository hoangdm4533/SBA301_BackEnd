package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.PageResponse;
import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.dto.request.exam.ExamRequest;
import com.example.demologin.dto.request.exam.AddQuestionToExamRequest;
import com.example.demologin.service.ExamService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/exams")
@AllArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PostMapping
    @SecuredEndpoint("EXAM_CREATE")
    @ApiResponse(message = "Tạo exam thành công")
    public Object createExam(@Valid @RequestBody ExamRequest request) {
        return examService.createExam(request);
    }

    @GetMapping("/{id}")
    @ApiResponse(message = "Lấy thông tin exam thành công")
    public Object getExamById(@PathVariable Long id) {
        return examService.getExamById(id);
    }

    @GetMapping
    @PageResponse
    public Object getAllExams(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return examService.getAllExams(page, size, sortBy, sortDir);
    }

    @PutMapping("/{id}")
    @SecuredEndpoint("EXAM_UPDATE")
    @ApiResponse(message = "Cập nhật exam thành công")
    public Object updateExam(
            @PathVariable Long id,
            @Valid @RequestBody ExamRequest request) {
        return examService.updateExam(id, request);
    }

    @DeleteMapping("/{id}")
    @SecuredEndpoint("EXAM_DELETE")
    @ApiResponse(message = "Xóa exam thành công")
    public Object deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
        return null;
    }

    @GetMapping("/status/{status}")
    @PageResponse
    public Object getExamsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return examService.getExamsByStatus(status, page, size, sortBy, sortDir);
    }

    @GetMapping("/search")
    @PageResponse
    public Object searchExams(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Pageable pageable = PageRequest.of(page, size);
        return examService.searchExams(keyword, pageable);
    }

    @PostMapping("/{examId}/questions")
    @SecuredEndpoint("EXAM_QUESTION_ADD")
    @ApiResponse(message = "Thêm câu hỏi vào exam thành công")
    public Object addQuestionToExam(
            @PathVariable Long examId,
            @Valid @RequestBody AddQuestionToExamRequest request) {
        return examService.addQuestionToExam(examId, request);
    }

    @DeleteMapping("/{examId}/questions/{questionId}")
    @SecuredEndpoint("EXAM_QUESTION_REMOVE")
    @ApiResponse(message = "Xóa câu hỏi khỏi exam thành công")
    public Object removeQuestionFromExam(
            @PathVariable Long examId,
            @PathVariable Long questionId) {
        examService.removeQuestionFromExam(examId, questionId);
        return null;
    }

    @GetMapping("/{examId}/questions")
    @ApiResponse(message = "Lấy danh sách câu hỏi trong exam thành công")
    public Object getQuestionsInExam(@PathVariable Long examId) {
        return examService.getQuestionsInExam(examId);
    }

    @PutMapping("/{id}/publish")
    @SecuredEndpoint("EXAM_PUBLISH")
    @ApiResponse(message = "Publish exam thành công")
    public Object publishExam(@PathVariable Long id) {
        examService.publishExam(id);
        return null;
    }

    @PutMapping("/{id}/archive")
    @SecuredEndpoint("EXAM_ARCHIVE")
    @ApiResponse(message = "Archive exam thành công")
    public Object archiveExam(@PathVariable Long id) {
        examService.archiveExam(id);
        return null;
    }

    @GetMapping("/published")
    @PageResponse
    public Object getPublishedExams(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Pageable pageable = PageRequest.of(page, size);
        return examService.getPublishedExams(pageable);
    }
}