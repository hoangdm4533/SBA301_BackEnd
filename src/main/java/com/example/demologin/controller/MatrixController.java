package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.PageResponse;
import com.example.demologin.dto.request.matrix.MatrixRequest;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.dto.response.matrix.MatrixResponse;
import com.example.demologin.service.MatrixService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matrices")
@RequiredArgsConstructor
public class MatrixController {
    private final MatrixService matrixService;

    @PostMapping
    @ApiResponse(message = "Matrix created successfully")
    @Operation(summary = "Create matrix", description = "Create a new matrix")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> createMatrix(@RequestBody MatrixRequest request) {
        final MatrixResponse data = matrixService.createMatrix(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject(
                HttpStatus.CREATED.value(),
                "Matrix created successfully",
                data
        ));
    }

    @GetMapping
    @ApiResponse(message = "Matrices retrieved successfully")
    @PageResponse
    @Operation(summary = "Get all matrices", description = "Retrieve all matrices with pagination")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<ResponseObject> getAllMatrices(Pageable pageable) {
        final Page<MatrixResponse> data = matrixService.getAllMatrices(pageable);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Matrices retrieved successfully",
                data
        ));
    }

    @GetMapping("/{id}")
    @ApiResponse(message = "Matrix retrieved successfully")
    @Operation(summary = "Get matrix by ID", description = "Retrieve a matrix by its ID")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<ResponseObject> getMatrixById(@PathVariable Long id) {
        final MatrixResponse data = matrixService.getMatrixById(id);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Matrix retrieved successfully",
                data
        ));
    }

    @PutMapping("/{id}")
    @ApiResponse(message = "Matrix updated successfully")
    @Operation(summary = "Update matrix", description = "Update an existing matrix")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> updateMatrix(@PathVariable Long id,
                                                        @RequestBody MatrixRequest request) {
        final MatrixResponse data = matrixService.updateMatrix(id, request);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Matrix updated successfully",
                data
        ));
    }

    @DeleteMapping("/{id}")
    @ApiResponse(message = "Matrix deleted successfully")
    @Operation(summary = "Delete matrix", description = "Delete a matrix")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> deleteMatrix(@PathVariable Long id) {
        matrixService.deleteMatrix(id);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Matrix deleted successfully",
                id
        ));
    }
}