package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.PageResponse;
import com.example.demologin.dto.request.level.LevelRequest;
import com.example.demologin.dto.response.LevelResponse;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.service.LevelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/levels")
@Tag(name = "Level Management", description = "APIs for managing exam levels")
@AllArgsConstructor
public class LevelController {

    private final LevelService levelService;

    @PostMapping
    @ApiResponse(message = "Level created successfully")
    @Operation(summary = "Create new level", description = "Create a new exam level")
    public ResponseEntity<ResponseObject> createLevel(@Valid @RequestBody LevelRequest request) {
        final LevelResponse data = levelService.createLevel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject(
                HttpStatus.CREATED.value(),
                "Level created successfully",
                data
        ));
    }

    @GetMapping("/{id}")
    @ApiResponse(message = "Level retrieved successfully")
    @Operation(summary = "Get level by ID", description = "Retrieve a level by its ID")
    public ResponseEntity<ResponseObject> getLevelById(
            @Parameter(description = "Level ID") @PathVariable Long id) {
        final LevelResponse data = levelService.getLevelById(id);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Level retrieved successfully",
                data
        ));
    }

    @GetMapping
    @PageResponse
    @ApiResponse(message = "Levels retrieved successfully")
    @Operation(summary = "Get all levels", description = "Retrieve all levels with pagination")
    public ResponseEntity<ResponseObject> getAllLevels(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "minScore") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        final Page<LevelResponse> data = levelService.getAllLevels(page, size, sortBy, sortDir);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Levels retrieved successfully",
                data
        ));
    }

    @GetMapping("{difficulty}")
    @PageResponse
    @ApiResponse(message = "Level find by difficulty completed successfully")
    @Operation(summary = "Get level", description = "Get level by difficulty")
    public ResponseEntity<ResponseObject> findByDifficulty(@PathVariable("difficulty") String difficulty) {
        final LevelResponse data = levelService.getByDifficulty(difficulty);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Levels find by difficulty completed successfully",
                data
        ));
    }

    @GetMapping("/search")
    @PageResponse
    @ApiResponse(message = "Levels search completed successfully")
    @Operation(summary = "Search levels", description = "Search levels by keyword")
    public ResponseEntity<ResponseObject> searchLevels(
            @Parameter(description = "Search keyword") @RequestParam String keyword,
            @Parameter(description = "Pagination information") Pageable pageable) {
        final Page<LevelResponse> data = levelService.searchLevels(keyword, pageable);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Levels search completed successfully",
                data
        ));
    }

    @PutMapping("/{id}")
    @ApiResponse(message = "Level updated successfully")
    @Operation(summary = "Update level", description = "Update an existing level")
    public ResponseEntity<ResponseObject> updateLevel(
            @Parameter(description = "Level ID") @PathVariable Long id,
            @Valid @RequestBody LevelRequest request) {
        final LevelResponse data = levelService.updateLevel(id, request);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Level updated successfully",
                data
        ));
    }

    @DeleteMapping("/{id}")
    @ApiResponse(message = "Level deleted successfully")
    @Operation(summary = "Delete level", description = "Delete a level")
    public ResponseEntity<ResponseObject> deleteLevel(
            @Parameter(description = "Level ID") @PathVariable Long id) {
        levelService.deleteLevel(id);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Level deleted successfully",
                id
        ));
    }

}