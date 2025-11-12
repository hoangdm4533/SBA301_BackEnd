package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.AuthenticatedEndpoint;
import com.example.demologin.annotation.PageResponse;
import com.example.demologin.dto.request.exam.ExamSubmitRequest;
import com.example.demologin.dto.response.AttemptDetailResponse;
import com.example.demologin.dto.response.AttemptSummary;
import com.example.demologin.dto.response.ExamCard;
import com.example.demologin.dto.response.ExamStartResponse;
import com.example.demologin.dto.response.ExamSubmitResponse;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.service.ExamTakingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exams")
@Tag(name = "Exam Taking (Member)", description = "APIs for members to view and take exams")
public class ExamMemberController {

    private final ExamTakingService examTakingService;

    @GetMapping("/available")
    @AuthenticatedEndpoint
    @PreAuthorize("hasRole('STUDENT')")
    @PageResponse
    @ApiResponse(message = "Lấy danh sách bài thi khả dụng thành công")
    public ResponseEntity<ResponseObject> available(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size
    ) {
        Page<ExamCard> data = examTakingService.listAvailable(page, size);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Available exams retrieved",
                data
        ));
    }

    // Bắt đầu làm bài thi → tạo attempt, trả về đề (ẩn đáp án đúng nếu có cấu hình)
    @PostMapping("/{examId}/start")
    @AuthenticatedEndpoint
    @PreAuthorize("hasRole('STUDENT')")
    @ApiResponse(message = "Bắt đầu làm bài thi thành công")
    public ResponseEntity<ResponseObject> start(@PathVariable final Long examId) {
        ExamStartResponse data = examTakingService.startAttempt(examId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject(
                HttpStatus.CREATED.value(),
                "Attempt started",
                data
        ));
    }

    // Nộp bài và chấm tự động
    @PostMapping("/attempts/{attemptId}/submit")
    @AuthenticatedEndpoint
    @PreAuthorize("hasRole('STUDENT')")
    @ApiResponse(message = "Nộp bài & chấm điểm thành công")
    public ResponseEntity<ResponseObject> submit(
            @PathVariable final Long attemptId,
            @Valid @RequestBody final ExamSubmitRequest req
    ) {
        ExamSubmitResponse data = examTakingService.submitAttempt(attemptId, req);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Attempt submitted & graded",
                data
        ));
    }

    // Lịch sử attempt của chính người dùng
    @GetMapping("/my/attempts")
    @AuthenticatedEndpoint
    @PreAuthorize("hasRole('STUDENT')")
    @PageResponse
    @ApiResponse(message = "Lấy danh sách attempt của người dùng thành công")
    public ResponseEntity<ResponseObject> myAttempts(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size
    ) {
        Page<AttemptSummary> data = examTakingService.myAttempts(page, size);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "My attempts retrieved",
                data
        ));
    }

    // Xem chi tiết bài thi đã làm (câu đúng/sai, điểm từng câu, ...)
    @GetMapping("/attempts/{attemptId}")
    @AuthenticatedEndpoint
    @PreAuthorize("hasRole('STUDENT')")
    @ApiResponse(message = "Lấy chi tiết bài thi thành công")
    public ResponseEntity<ResponseObject> getAttemptDetail(@PathVariable final Long attemptId) {
        AttemptDetailResponse data = examTakingService.getAttemptDetail(attemptId);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Attempt details retrieved",
                data
        ));
    }
}
