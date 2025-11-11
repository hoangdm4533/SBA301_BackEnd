package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.dto.request.lesson.LessonRequest;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.dto.response.lesson.LessonResponse;
import com.example.demologin.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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
    @ApiResponse(message = "Lesson created successfully")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> createLesson(@RequestBody LessonRequest request) {
        final LessonResponse data = lessonService.createLesson(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject(
                HttpStatus.CREATED.value(),
                "Lesson created successfully",
                data
        ));
    }

    @GetMapping("/{id}")
    @ApiResponse(message = "Lesson retrieved successfully")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getLessonById(@PathVariable Long id) {
        final LessonResponse data = lessonService.getLessonById(id);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Lesson retrieved successfully",
                data
        ));
    }

    @GetMapping
    @ApiResponse(message = "Lessons retrieved successfully")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<ResponseObject> getLessons(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "id") String sort,
            @RequestParam(required = false, defaultValue = "asc") String direction
    ) {
        // Nếu không có tham số page → trả full list
        if (page == null) {
            List<LessonResponse> lessons = lessonService.getAllLessons();
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.value(),
                    "All lessons retrieved successfully",
                    lessons
            ));
        }

        // Nếu có tham số page → trả dữ liệu phân trang
        Sort sortOrder = direction.equalsIgnoreCase("desc")
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();

        Pageable pageable = PageRequest.of(page, size, sortOrder);
        Page<LessonResponse> pagedLessons = lessonService.getLessonsPage(pageable);

        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Lessons retrieved successfully",
                pagedLessons
        ));
    }

    @PutMapping("/{id}")
    @ApiResponse(message = "Lesson updated successfully")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> updateLesson(@PathVariable Long id, @RequestBody LessonRequest request) {
        final LessonResponse data = lessonService.updateLesson(id, request);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Lesson updated successfully",
                data
        ));
    }

    @DeleteMapping("/{id}")
    @ApiResponse(message = "Lesson deleted successfully")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Lesson deleted successfully",
                id
        ));
    }

}
