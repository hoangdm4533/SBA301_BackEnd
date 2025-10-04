package com.example.demologin.service;

import com.example.demologin.dto.request.lesson_plan.LessonPlanRequest;
import com.example.demologin.dto.response.LessonPlanResponse;
import com.example.demologin.entity.LessonPlan;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LessonPlanService {
    LessonPlanResponse createLessonPlan(LessonPlanRequest req);
    LessonPlanResponse mapToResponse(LessonPlan plan);
    void deleteLessonPlan(Long lessonPlanId);
    LessonPlanResponse findLessonPlanById(Long lessonPlanId);
    List<LessonPlanResponse> getAllLessonPlans();
    Page<LessonPlanResponse> getLessonPlans(int page, int size, String sortBy, String sortDir);
}
