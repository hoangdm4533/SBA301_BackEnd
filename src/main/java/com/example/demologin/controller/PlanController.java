package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.PageResponse;
import com.example.demologin.dto.request.plan.PlanRequest;
import com.example.demologin.dto.response.PlanResponse;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
@Tag(name = "Plan Management", description = "CRUD operations for Plans")
public class PlanController {

    private final PlanService planService;

    @PostMapping
    @ApiResponse(message = "Plan created successfully")
    @Operation(summary = "Tạo mới Plan", description = "Thêm gói học mới")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> create(@RequestBody PlanRequest request) {
        final PlanResponse data = planService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject(
                HttpStatus.CREATED.value(),
                "Plan created successfully",
                data
        ));
    }

    @PutMapping("/{id}")
    @ApiResponse(message = "Plan updated successfully")
    @Operation(summary = "Cập nhật Plan", description = "Sửa thông tin gói học")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> update(@PathVariable Long id, @RequestBody PlanRequest request) {
        final PlanResponse data = planService.update(id, request);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Plan updated successfully",
                data
        ));
    }

    @DeleteMapping("/{id}")
    @ApiResponse(message = "Plan deleted successfully")
    @Operation(summary = "Xóa Plan", description = "Xóa gói học theo ID")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> delete(@PathVariable Long id) {
        planService.delete(id);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Plan deleted successfully",
                id
        ));
    }

    @GetMapping("/{id}")
    @ApiResponse(message = "Plan retrieved successfully")
    @Operation(summary = "Xem chi tiết Plan", description = "Lấy thông tin gói học theo ID")
    public ResponseEntity<ResponseObject> getById(@PathVariable Long id) {
        final PlanResponse data = planService.getById(id);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Plan retrieved successfully",
                data
        ));
    }

    @GetMapping
    @ApiResponse(message = "Plans retrieved successfully")
    @Operation(summary = "Lấy danh sách Plan", description = "Trả về danh sách tất cả các gói học")
    public ResponseEntity<ResponseObject> getAll() {
        final List<PlanResponse> data = planService.getAll();
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Plans retrieved successfully",
                data
        ));
    }

    @GetMapping("/paged")
    @ApiResponse(message = "Plans retrieved successfully")
    @PageResponse
    @Operation(summary = "Lấy danh sách Plan (phân trang)", description = "Trả về danh sách các gói học có phân trang")
    public ResponseEntity<ResponseObject> getAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        final com.example.demologin.dto.response.PageResponse<PlanResponse> data = planService.getAllPaged(pageable);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Plans retrieved successfully",
                data
        ));
    }
}