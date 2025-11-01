package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.dto.request.lesson.LessonRequest;
import com.example.demologin.dto.response.lesson.LessonResponse;
import com.example.demologin.service.LessonService;
import lombok.RequiredArgsConstructor;
import okhttp3.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {
    private final LessonService lessonService;

    @PostMapping
    @ApiResponse(message = "Create lesson")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<LessonResponse> createLesson(@RequestBody LessonRequest request) {
        return ResponseEntity.ok(lessonService.createLesson(request));
    }

    @GetMapping("/{id}")
    @ApiResponse(message = "Create lesson")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<LessonResponse> getLessonById(@PathVariable Long id) {
        return ResponseEntity.ok(lessonService.getLessonById(id));
    }

    @GetMapping
    @ApiResponse(message = "Get all lessons or paginated lessons")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<?> getLessons(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "id") String sort,
            @RequestParam(required = false, defaultValue = "asc") String direction
    ) {
        // ✅ Nếu không có tham số page → trả full list
        if (page == null) {
            List<LessonResponse> lessons = lessonService.getAllLessons();
            return ResponseEntity.ok(lessons);
        }

        // ✅ Nếu có tham số page → trả dữ liệu phân trang
        Sort sortOrder = direction.equalsIgnoreCase("desc")
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();

        Pageable pageable = PageRequest.of(page, size, sortOrder);
        Page<LessonResponse> pagedLessons = lessonService.getLessonsPage(pageable);

        return ResponseEntity.ok(pagedLessons);
    }

    @PutMapping("/{id}")
    @ApiResponse(message = "Create lesson")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<LessonResponse> updateLesson(@PathVariable Long id, @RequestBody LessonRequest request) {
        return ResponseEntity.ok(lessonService.updateLesson(id, request));
    }

    @DeleteMapping("/{id}")
    @ApiResponse(message = "Create lesson")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }

}
