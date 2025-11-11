package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.annotation.SmartCache;
import com.example.demologin.dto.request.lesson_plan.LessonPlanEditRequest;
import com.example.demologin.dto.response.LessonPlanEditResponse;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.service.LessonPlanEditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lesson-plan-edits")
@RequiredArgsConstructor
@Tag(name = "Lesson Plan Edit Management", description = "Track lesson plan edit history")
public class LessonPlanEditController {
    private final LessonPlanEditService editService;

    @PostMapping
    @ApiResponse(message = "Lesson plan edit created successfully")
    @SecuredEndpoint("LESSON_PLAN_EDIT_CREATE")
    @SmartCache
    @Operation(summary = "Create new lesson plan edit", description = "Create a lesson plan edit record")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> create(@RequestBody LessonPlanEditRequest req) {
        final LessonPlanEditResponse data = editService.saveEdit(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject(
                HttpStatus.CREATED.value(),
                "Lesson plan edit created successfully",
                data
        ));
    }

    @GetMapping("/{lessonPlanId}")
    @ApiResponse(message = "Lesson plan edits retrieved successfully")
    @SecuredEndpoint("LESSON_PLAN_EDIT_VIEW")
    @SmartCache
    @Operation(summary = "Get lesson plan edit history", description = "Get all edits for a specific lesson plan")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getAll(@PathVariable Long lessonPlanId) {
        final List<LessonPlanEditResponse> data = editService.getEdits(lessonPlanId);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Lesson plan edits retrieved successfully",
                data
        ));
    }
}
