package com.example.demologin.controller;


import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.PageResponse;
import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.service.ExamService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exams")
@Tag(name = "Exam Results", description = "APIs for teacher to view student scores")
public class ExamResultController {
    private final ExamService examService;


    @GetMapping("/{examId}/attempts")
    @SecuredEndpoint("EXAM_RESULT_VIEW")
    @PageResponse
    @ApiResponse(message = "Lấy danh sách bài làm của học sinh thành công")
    public ResponseEntity<ResponseObject> listAttemptsOfExam(
            @PathVariable Long examId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        // Quy ước: from = đầu ngày; to = cuối ngày (exclusive = +1 day at 00:00)
        LocalDateTime fromDt = (from != null) ? from.atStartOfDay() : null;
        LocalDateTime toDt   = (to   != null) ? to.plusDays(1).atStartOfDay() : null;

        var data = examService.listAttemptsOfExam(examId, page, size, keyword, fromDt, toDt);
        return ResponseEntity.ok(new ResponseObject(200, "Lấy danh sách bài làm của học sinh thành công", data));
    }

    @GetMapping("/students/{studentId}/attempts")
    @SecuredEndpoint("EXAM_RESULT_VIEW")
    @PageResponse
    @ApiResponse(message = "Lấy lịch sử làm bài của học sinh thành công")
    public ResponseEntity<ResponseObject> attemptsOfStudent(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        var data = examService.listAttemptsOfStudent(studentId, page, size);
        return ResponseEntity.ok(new ResponseObject(
                200,
                "Lấy lịch sử làm bài của học sinh thành công",
                data
        ));
    }
}
