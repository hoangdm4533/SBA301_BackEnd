package com.example.demologin.controller;

import com.example.demologin.dto.request.plan.PlanRequest;
import com.example.demologin.dto.response.PageResponse;
import com.example.demologin.dto.response.PlanResponse;
import com.example.demologin.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
@Tag(name = "Plan Management", description = "CRUD operations for Plans")
public class PlanController {

    private final PlanService planService;

    @PostMapping
    @Operation(summary = "Tạo mới Plan", description = "Thêm gói học mới")
    public PlanResponse create(@RequestBody PlanRequest request) {
        return planService.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật Plan", description = "Sửa thông tin gói học")
    public PlanResponse update(@PathVariable Long id, @RequestBody PlanRequest request) {
        return planService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa Plan", description = "Xóa gói học theo ID")
    public void delete(@PathVariable Long id) {
        planService.delete(id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Xem chi tiết Plan", description = "Lấy thông tin gói học theo ID")
    public PlanResponse getById(@PathVariable Long id) {
        return planService.getById(id);
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách Plan (phân trang)", description = "Trả về danh sách các gói học có phân trang")
    public List<PlanResponse> getAll() {
        return planService.getAll();
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách Plan (phân trang)", description = "Trả về danh sách các gói học có phân trang")
    public PageResponse<PlanResponse> getAll(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return planService.getAllPaged(pageable);
    }
}