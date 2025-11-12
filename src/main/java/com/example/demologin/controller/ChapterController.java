package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.dto.request.chapter.ChapterRequest;
import com.example.demologin.dto.response.ChapterResponse;
import com.example.demologin.dto.response.PageResponse;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.dto.response.lesson.LessonResponse;
import com.example.demologin.service.ChapterService;
import com.example.demologin.service.LessonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/chapters")
@RequiredArgsConstructor
@Tag(name = "Chapter Management", description = "CRUD operations for chapters")
public class ChapterController {

    private final ChapterService chapterService;
    private final LessonService lessonService;

    @PostMapping
    @ApiResponse(message = "Chapter created successfully")
    @Operation(summary = "Create new chapter", description = "Thêm mới một chương thuộc LessonPlan")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> create(@RequestBody ChapterRequest request) {
        final ChapterResponse data = chapterService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject(
                HttpStatus.CREATED.value(),
                "Chapter created successfully",
                data
        ));
    }

    @PutMapping("/{id}")
    @ApiResponse(message = "Chapter updated successfully")
    @Operation(summary = "Update chapter", description = "Cập nhật thông tin chương theo ID")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> update(@PathVariable Long id, @RequestBody ChapterRequest request) {
        final ChapterResponse data = chapterService.update(id, request);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Chapter updated successfully",
                data
        ));
    }

    @DeleteMapping("/{id}")
    @ApiResponse(message = "Chapter deleted successfully")
    @Operation(summary = "Delete chapter", description = "Xóa chương theo ID")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> delete(@PathVariable Long id) {
        chapterService.delete(id);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Chapter deleted successfully",
                id
        ));
    }

    @GetMapping("/{id}")
    @ApiResponse(message = "Chapter retrieved successfully")
    @Operation(summary = "Get chapter by ID", description = "Lấy chi tiết chương theo ID")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<ResponseObject> getById(@PathVariable Long id) {
        final ChapterResponse data = chapterService.getById(id);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Chapter retrieved successfully",
                data
        ));
    }

    @GetMapping
    @ApiResponse(message = "Chapters retrieved successfully")
    @Operation(summary = "Get all chapters (auto-detect paging)",
            description = "Nếu không có tham số page → trả toàn bộ danh sách chương; nếu có → trả dữ liệu phân trang")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<ResponseObject> getAllChapters(
            @RequestParam(required = false) Integer page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        // Nếu không có param page → trả toàn bộ danh sách
        if (page == null) {
            List<ChapterResponse> chapters = chapterService.getAll();
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.value(),
                    "All chapters retrieved successfully",
                    chapters
            ));
        }

        // Có param page → trả dữ liệu phân trang
        Sort sortOrder = direction.equalsIgnoreCase("desc")
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();

        Pageable pageable = PageRequest.of(page, size, sortOrder);
        com.example.demologin.dto.response.PageResponse<ChapterResponse> pageResponse = chapterService.getAllPaged(pageable);

        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Chapters retrieved successfully",
                pageResponse
        ));
    }

    @GetMapping("/{chapterId}/lessons")
    @ApiResponse(message = "Lessons retrieved successfully")
    @Operation(summary = "Get lessons by chapter", description = "Lấy danh sách bài học theo chương")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<ResponseObject> getLessonsByChapter(@PathVariable Long chapterId) {
        final List<LessonResponse> data = lessonService.getLessonsByChapterId(chapterId);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Lessons retrieved successfully",
                data
        ));
    }
}
