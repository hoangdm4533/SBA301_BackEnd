package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.dto.request.chapter.ChapterRequest;
import com.example.demologin.dto.response.ChapterResponse;
import com.example.demologin.dto.response.PageResponse;
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
    @Operation(summary = "Create new chapter", description = "Thêm mới một chương thuộc LessonPlan")
    public ChapterResponse create(@RequestBody ChapterRequest request) {
        return chapterService.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update chapter", description = "Cập nhật thông tin chương theo ID")
    public ChapterResponse update(@PathVariable Long id, @RequestBody ChapterRequest request) {
        return chapterService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete chapter", description = "Xóa chương theo ID")
    public void delete(@PathVariable Long id) {
        chapterService.delete(id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get chapter by ID", description = "Lấy chi tiết chương theo ID")
    public ChapterResponse getById(@PathVariable Long id) {
        return chapterService.getById(id);
    }


    @Operation(summary = "Get all chapters (auto-detect paging)",
            description = "Nếu không có tham số page → trả toàn bộ danh sách chương; nếu có → trả dữ liệu phân trang")
    @ApiResponse(message = "Get chapters successfully")
    @GetMapping
    public ResponseEntity<?> getAllChapters(
            @RequestParam(required = false) Integer page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        // ✅ Nếu không có param page → trả toàn bộ danh sách
        if (page == null) {
            List<ChapterResponse> chapters = chapterService.getAll();
            return ResponseEntity.ok(chapters);
        }

        // ✅ Có param page → trả dữ liệu phân trang
        Sort sortOrder = direction.equalsIgnoreCase("desc")
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();

        Pageable pageable = PageRequest.of(page, size, sortOrder);
        PageResponse<ChapterResponse> pageResponse = chapterService.getAllPaged(pageable);

        return ResponseEntity.ok(pageResponse);
    }

    @GetMapping("/{chapterId}/lessons")
    @ApiResponse(message = "Get lessons by chapter id")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<List<LessonResponse>> getLessonsByChapter(@PathVariable Long chapterId) {
        List<LessonResponse> lessons = lessonService.getLessonsByChapterId(chapterId);
        return ResponseEntity.ok(lessons);
    }
}
