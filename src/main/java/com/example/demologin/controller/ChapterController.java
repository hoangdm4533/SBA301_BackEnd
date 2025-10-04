package com.example.demologin.controller;

import com.example.demologin.dto.request.chapter.ChapterRequest;
import com.example.demologin.dto.response.ChapterResponse;
import com.example.demologin.dto.response.PageResponse;
import com.example.demologin.service.ChapterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/chapters")
@RequiredArgsConstructor
@Tag(name = "Chapter Management", description = "CRUD operations for chapters")
public class ChapterController {

    private final ChapterService chapterService;

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

    @Operation(summary = "Get all chapters (paged)", description = "Lấy danh sách chương có phân trang")
    @GetMapping("paged")
    public PageResponse<ChapterResponse> getAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return chapterService.getAllPaged(pageable);
    }

    @Operation(summary = "Get all chapters (no pagination)", description = "Lấy toàn bộ danh sách chương (không phân trang)")
    @GetMapping()
    public List<ChapterResponse> getAll() {
        return chapterService.getAll();
    }
}
