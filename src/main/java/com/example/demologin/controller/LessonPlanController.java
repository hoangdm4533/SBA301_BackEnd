package com.example.demologin.controller;

import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.annotation.SmartCache;
import com.example.demologin.dto.request.lesson_plan.LessonPlanRequest;
import com.example.demologin.dto.response.LessonPlanResponse;
import com.example.demologin.service.LessonPlanService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lesson-plans")
public class LessonPlanController {
    private final LessonPlanService lessonPlanService;

    public LessonPlanController(LessonPlanService lessonPlanService) {
        this.lessonPlanService = lessonPlanService;
    }

    @PostMapping
    @SecuredEndpoint("LESSON_PLAN_CREATE")
    @SmartCache
    @Operation(summary = "Create new lesson plan",
            description = "Create a lesson plan")
    public LessonPlanResponse create(@RequestBody LessonPlanRequest req) {
        return lessonPlanService.createLessonPlan(req);
    }


}
