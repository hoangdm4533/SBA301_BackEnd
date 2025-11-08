package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.annotation.SmartCache;
import com.example.demologin.dto.request.lesson_plan.LessonPlanRequest;
import com.example.demologin.dto.response.LessonPlanResponse;
import com.example.demologin.service.LessonPlanCompactionService;
import com.example.demologin.service.LessonPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lesson-plans")
@RequiredArgsConstructor
public class LessonPlanController {
    private final LessonPlanService lessonPlanService;
    private final LessonPlanCompactionService compactionService;


    @PostMapping
    @SmartCache
    @Operation(summary = "Create new lesson plan",
            description = "Create a lesson plan")
    public LessonPlanResponse create(@RequestBody LessonPlanRequest req) {
        return lessonPlanService.createLessonPlan(req);
    }

    @PostMapping("/{lessonPlanId}/save")
    public ResponseEntity<String> compactAndSave(@PathVariable Long lessonPlanId) {
        try {
            compactionService.compactLessonPlan(lessonPlanId);
            return ResponseEntity.ok("Lesson plan " + lessonPlanId + " compacted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to compact lesson plan " + lessonPlanId + ": " + e.getMessage());
        }
    }

    // ----------------------------
    // GET BY ID
    // ----------------------------
    @GetMapping("/{lessonPlanId}")
    @Operation(summary = "Get lesson plan by ID", description = "Retrieve a lesson plan and load content from MinIO if available.")
    public ResponseEntity<LessonPlanResponse> getById(@PathVariable Long lessonPlanId) {
        LessonPlanResponse response = lessonPlanService.findLessonPlanById(lessonPlanId);
        return ResponseEntity.ok(response);
    }

    // ----------------------------
    // GET ALL (no pagination)
    // ----------------------------
    @GetMapping()
    @Operation(summary = "Get all lesson plans", description = "Return a list of all lesson plans (no pagination).")
    public ResponseEntity<List<LessonPlanResponse>> getAll() {
        List<LessonPlanResponse> response = lessonPlanService.getAllLessonPlans();
        return ResponseEntity.ok(response);
    }

    // ----------------------------
    // GET PAGEABLE (with pagination + sort)
    // ----------------------------
    @GetMapping("paged")
    @Operation(summary = "Get paginated lesson plans", description = "Return lesson plans with pagination and sorting options.")
    public Page<LessonPlanResponse> getPaged(
            @Parameter(description = "Page number, starts from 0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction: asc or desc") @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Page<LessonPlanResponse> response = lessonPlanService.getLessonPlans(page, size, sortBy, sortDir);
        return response;
    }

    // ----------------------------
    // DELETE
    // ----------------------------
    @DeleteMapping("/{lessonPlanId}")
    @ApiResponse(message = "Delete lesson plan successfully")
    @Operation(summary = "Delete lesson plan", description = "Delete a lesson plan and its MinIO file.")
    public ResponseEntity<String> delete(@PathVariable Long lessonPlanId) {
        try {
            lessonPlanService.deleteLessonPlan(lessonPlanId);
            return ResponseEntity.ok("Lesson plan " + lessonPlanId + " deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete lesson plan " + lessonPlanId + ": " + e.getMessage());
        }
    }


    @Operation(summary = "Update a lesson plan by ID")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Lesson plan updated successfully"),
//            @ApiResponse(responseCode = "404", description = "Lesson plan not found")
//    })
    @ApiResponse(message = "Update lesson successfully")
    @PutMapping("/{id}")
    public ResponseEntity<LessonPlanResponse> updateLessonPlan(
            @PathVariable("id") Long lessonPlanId,
            @RequestBody LessonPlanRequest request
    ) {
        LessonPlanResponse response = lessonPlanService.updateLessonPlan(lessonPlanId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/export")
    public ResponseEntity<byte[]> exportLessonPlan(@PathVariable Long id) {
        try {
            byte[] wordData = lessonPlanService.exportLessonPlanToWord(id);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=lessonplan_" + id + ".docx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(wordData);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }


}
