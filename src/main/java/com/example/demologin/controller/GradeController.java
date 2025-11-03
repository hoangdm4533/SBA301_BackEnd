package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.dto.request.grade.GradeRequest;
import com.example.demologin.dto.response.ChapterResponse;
import com.example.demologin.dto.response.GradeResponse;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.service.GradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grades")
@Tag(name = "Grade Management", description = "APIs for managing grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    // Create
    @PostMapping
    @ApiResponse(message = "Grade created successfully")
    @Operation(summary = "Create new grade", description = "Create a new grade")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> create(@Valid @RequestBody GradeRequest request) {
        GradeResponse data = gradeService.createGrade(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseObject(HttpStatus.CREATED.value(), "Grade created successfully", data));
    }

    // Get by ID
    @GetMapping("/{id}")
    @ApiResponse(message = "Grade retrieved successfully")
    @Operation(summary = "Get grade by ID", description = "Retrieve a grade by its ID")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<ResponseObject> getById(
            @Parameter(description = "Grade ID") @PathVariable Long id) {
        GradeResponse data = gradeService.getGradeById(id);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Grade retrieved successfully", data));
    }

    // List all
    @GetMapping
    @ApiResponse(message = "Grades retrieved successfully")
    @Operation(summary = "Get all grades", description = "Retrieve all grades")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<ResponseObject> getAll() {
        List<GradeResponse> data = gradeService.getAllGrades();
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Grades retrieved successfully", data));
    }

    // Update
    @PutMapping("/{id}")
    @ApiResponse(message = "Grade updated successfully")
    @Operation(summary = "Update grade", description = "Update an existing grade")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> update(
            @Parameter(description = "Grade ID") @PathVariable Long id,
            @Valid @RequestBody GradeRequest request) {
        GradeResponse data = gradeService.updateGrade(id, request);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Grade updated successfully", data));
    }

    // Delete
    @DeleteMapping("/{id}")
    @ApiResponse(message = "Grade deleted successfully")
    @Operation(summary = "Delete grade", description = "Delete a grade")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> delete(
            @Parameter(description = "Grade ID") @PathVariable Long id) {
        gradeService.deleteGrade(id);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Grade deleted successfully", id));
    }

    // Get chapters by grade
    @GetMapping("/{gradeId}/chapters")
    @ApiResponse(message = "Get list chapters by grade id successfully")
    @Operation(summary = "Get chapters by grade", description = "Retrieve chapters by grade id")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<ResponseObject> getChaptersByGrade(@PathVariable Long gradeId) {
        List<ChapterResponse> data = gradeService.getChaptersByGradeId(gradeId);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Chapters retrieved successfully", data));
    }
}
