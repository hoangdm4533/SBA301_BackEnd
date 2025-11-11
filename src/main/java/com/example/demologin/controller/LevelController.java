package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.PageResponse;
import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.dto.request.level.LevelRequest;
import com.example.demologin.dto.response.LevelResponse;
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

import java.util.List;

@RestController
@RequestMapping("/api/levels")
@Tag(name = "Level Management", description = "APIs for managing exam levels")
@AllArgsConstructor
public class LevelController {

    private final LevelService levelService;

    @PostMapping
    @ApiResponse(message = "Level created successfully")
    @Operation(summary = "Create new level", description = "Create a new exam level")
    public Object createLevel(@Valid @RequestBody LevelRequest request) {
        return levelService.createLevel(request);
    }

    @GetMapping("/{id}")
    @ApiResponse(message = "Level retrieved successfully")
    @Operation(summary = "Get level by ID", description = "Retrieve a level by its ID")
    public Object getLevelById(
            @Parameter(description = "Level ID") @PathVariable Long id) {
        return levelService.getLevelById(id);
    }

    @GetMapping
    @PageResponse
    @ApiResponse(message = "Levels retrieved successfully")
    @Operation(summary = "Get all levels", description = "Retrieve all levels with pagination")
    public Object getAllLevels(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "minScore") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        return levelService.getAllLevels(page, size, sortBy, sortDir);
    }

    @GetMapping("/search")
    @PageResponse
    @ApiResponse(message = "Levels search completed successfully")
    @Operation(summary = "Search levels", description = "Search levels by keyword")
    public Object searchLevels(
            @Parameter(description = "Search keyword") @RequestParam String keyword,
            @Parameter(description = "Pagination information") Pageable pageable) {
        return levelService.searchLevels(keyword, pageable);
    }

    @PutMapping("/{id}")
    @ApiResponse(message = "Level updated successfully")
    @Operation(summary = "Update level", description = "Update an existing level")
    public Object updateLevel(
            @Parameter(description = "Level ID") @PathVariable Long id,
            @Valid @RequestBody LevelRequest request) {
        return levelService.updateLevel(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete level", description = "Delete a level")
    public ResponseEntity<String> deleteLevel(
            @Parameter(description = "Level ID") @PathVariable Long id) {
        levelService.deleteLevel(id);
        return ResponseEntity.ok("Level deleted successfully");
    }





}