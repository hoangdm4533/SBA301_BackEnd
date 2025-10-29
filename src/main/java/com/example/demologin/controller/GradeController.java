package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.dto.request.grade.GradeRequest;
import com.example.demologin.dto.response.GradeResponse;
import com.example.demologin.service.GradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grades")
@Tag(name = "Grade Management", description = "APIs for managing grades")
public class GradeController {
    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @PostMapping
    @SecuredEndpoint("GRADE_CREATE")
    @ApiResponse(message = "Grade created successfully")
    @Operation(summary = "Create new grade", description = "Create a new grade")
    public ResponseEntity<GradeResponse> create(@Valid @RequestBody GradeRequest request) {
        GradeResponse response = gradeService.createGrade(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
//    @SecuredEndpoint("GRADE_VIEW")
    @ApiResponse(message = "Grade retrieved successfully")
    @Operation(summary = "Get grade by ID", description = "Retrieve a grade by its ID")
    public ResponseEntity<GradeResponse> getById(
            @Parameter(description = "Grade ID") @PathVariable Long id) {
        return ResponseEntity.ok(gradeService.getGradeById(id));
    }

    @GetMapping
//    @SecuredEndpoint("GRADE_VIEW")
    @ApiResponse(message = "Grades retrieved successfully")
    @Operation(summary = "Get all grades", description = "Retrieve all grades")
    public ResponseEntity<List<GradeResponse>> getAll() {
        return ResponseEntity.ok(gradeService.getAllGrades());
    }

    @PutMapping("/{id}")
    @SecuredEndpoint("GRADE_UPDATE")
    @ApiResponse(message = "Grade updated successfully")
    @Operation(summary = "Update grade", description = "Update an existing grade")
    public ResponseEntity<GradeResponse> update(
            @Parameter(description = "Grade ID") @PathVariable Long id,
            @Valid @RequestBody GradeRequest request) {
        return ResponseEntity.ok(gradeService.updateGrade(id, request));
    }

    @DeleteMapping("/{id}")
    @SecuredEndpoint("GRADE_DELETE")
    @ApiResponse(message = "Grade deleted successfully")
    @Operation(summary = "Delete grade", description = "Delete a grade")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Grade ID") @PathVariable Long id) {
        gradeService.deleteGrade(id);
        return ResponseEntity.noContent().build();
    }
}
