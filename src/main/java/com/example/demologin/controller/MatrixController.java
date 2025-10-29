package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.PageResponse;
import com.example.demologin.dto.request.matrix.MatrixRequest;
import com.example.demologin.dto.response.matrix.MatrixResponse;
import com.example.demologin.service.MatrixService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matrices")
@RequiredArgsConstructor
public class MatrixController {
    private final MatrixService matrixService;

    @PostMapping
//    @ApiResponse(message = "Matrix created successfully")
    public MatrixResponse createMatrix(@RequestBody MatrixRequest request) {
        return matrixService.createMatrix(request);
    }

    @GetMapping
//    @ApiResponse(message = "Matrices retrieved successfully")
//    @PageResponse
    public Page<MatrixResponse> getAllMatrices(Pageable pageable) {
        return matrixService.getAllMatrices(pageable);
    }

    @GetMapping("/{id}")
    @ApiResponse(message = "Matrix retrieved successfully")
    public MatrixResponse getMatrixById(@PathVariable Long id) {
        return matrixService.getMatrixById(id);
    }

    @PutMapping("/{id}")
    @ApiResponse(message = "Matrix updated successfully")
    public MatrixResponse updateMatrix(@PathVariable Long id,
                                                       @RequestBody MatrixRequest request) {
        return matrixService.updateMatrix(id, request);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(message = "Matrix deleted successfully")
    public ResponseEntity<Void> deleteMatrix(@PathVariable Long id) {
        matrixService.deleteMatrix(id);
        return ResponseEntity.ok().build();
    }
}