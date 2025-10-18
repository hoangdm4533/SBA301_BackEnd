package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.AuthenticatedEndpoint;
import com.example.demologin.annotation.PageResponse;
import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.dto.request.exam.ExamSubmitRequest;
import com.example.demologin.dto.response.*;
import com.example.demologin.service.ExamTakingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exams")
@Tag(name = "Exam Taking (Member)", description = "APIs for members to view and take exams")
public class ExamMemberController {
    private final ExamTakingService examTakingService;

    // Danh sách bài thi có thể làm (PUBLISHED)
    @GetMapping("/available")
    @AuthenticatedEndpoint
    @SecuredEndpoint("EXAM_VIEW_AVAILABLE")
    @PageResponse
    public ResponseEntity<ResponseObject> available(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ExamCard> data = examTakingService.listAvailable(page, size);
        return ResponseEntity.ok(new ResponseObject(200, "Available exams retrieved", data));
    }

    // Bắt đầu làm bài thi → tạo attempt, trả về đề (nếu bạn muốn ẩn đáp án đúng)
    @PostMapping("/{examId}/start")
    @AuthenticatedEndpoint
    @SecuredEndpoint("EXAM_TAKE")
    @ApiResponse(message = "Bắt đầu làm bài thi thành công")
    public ResponseEntity<ResponseObject> start(@PathVariable Long examId) {
        ExamStartResponse data = examTakingService.startAttempt(examId);
        return ResponseEntity.status(201).body(new ResponseObject(201, "Attempt started", data));
    }

    // Nộp bài và chấm tự động
    @PostMapping("/attempts/{attemptId}/submit")
    @AuthenticatedEndpoint
    @SecuredEndpoint("EXAM_TAKE")
    @ApiResponse(message = "Nộp bài & chấm điểm thành công")
    public ResponseEntity<ResponseObject> submit(
            @PathVariable Long attemptId,
            @Valid @RequestBody ExamSubmitRequest req
    ) {
        ExamSubmitResponse data = examTakingService.submitAttempt(attemptId, req);
        return ResponseEntity.ok(new ResponseObject(200, "Attempt submitted & graded", data));
    }

    // Lịch sử bài làm của tôi
    @GetMapping("/my/attempts")
    @AuthenticatedEndpoint
    @SecuredEndpoint("EXAM_VIEW_HISTORY")
    @PageResponse
    public ResponseEntity<ResponseObject> myAttempts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<AttemptSummary> data = examTakingService.myAttempts(page, size);
        return ResponseEntity.ok(new ResponseObject(200, "My attempts retrieved", data));
    }
}
