package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.PageResponse;
import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.service.ExamService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exams")
@Tag(name = "Exam Results", description = "APIs for teacher to view student scores")
public class ExamResultController {

    private final ExamService examService;

    // Danh sách bài làm của học sinh theo bài thi
    @GetMapping("/{examId}/attempts")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @PageResponse
    @ApiResponse(message = "Lấy danh sách bài làm của học sinh thành công")
    public ResponseEntity<ResponseObject> listAttemptsOfExam(
            @PathVariable final Long examId,
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size,
            @RequestParam(required = false) final String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate to
    ) {
        // Quy ước: from = đầu ngày; to = cuối ngày (exclusive = +1 day at 00:00)
        final LocalDateTime fromDt = (from != null) ? from.atStartOfDay() : null;
        final LocalDateTime toDt   = (to != null)   ? to.plusDays(1).atStartOfDay() : null;

        final var data = examService.listAttemptsOfExam(examId, page, size, keyword, fromDt, toDt);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Lấy danh sách bài làm của học sinh thành công",
                data
        ));
    }

    // Lịch sử làm bài của một học sinh
    @GetMapping("/students/{studentId}/attempts")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @PageResponse
    @ApiResponse(message = "Lấy lịch sử làm bài của học sinh thành công")
    public ResponseEntity<ResponseObject> attemptsOfStudent(
            @PathVariable final Long studentId,
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size
    ) {
        final var data = examService.listAttemptsOfStudent(studentId, page, size);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Lấy lịch sử làm bài của học sinh thành công",
                data
        ));
    }
}
