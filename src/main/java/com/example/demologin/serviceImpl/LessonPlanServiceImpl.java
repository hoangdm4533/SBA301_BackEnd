package com.example.demologin.serviceImpl;

import com.example.demologin.dto.request.lesson_plan.LessonPlanRequest;
import com.example.demologin.dto.response.LessonPlanResponse;
import com.example.demologin.entity.Grade;
import com.example.demologin.entity.LessonPlan;
import com.example.demologin.entity.User;
import com.example.demologin.repository.GradeRepository;
import com.example.demologin.repository.LessonPlanRepository;
import com.example.demologin.repository.UserRepository;
import com.example.demologin.service.LessonPlanService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LessonPlanServiceImpl implements LessonPlanService {
    private final LessonPlanRepository lessonPlanRepo;
    private final UserRepository userRepo;
    private final GradeRepository gradeRepo;

    public LessonPlanServiceImpl(LessonPlanRepository lessonPlanRepo,
                             UserRepository userRepo,
                             GradeRepository gradeRepo) {
        this.lessonPlanRepo = lessonPlanRepo;
        this.userRepo = userRepo;
        this.gradeRepo = gradeRepo;
    }

    @Override
    public LessonPlanResponse createLessonPlan(LessonPlanRequest req) {
        User teacher = userRepo.findById(req.getTeacherId())
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found"));
        Grade grade = gradeRepo.findById(req.getGradeId())
                .orElseThrow(() -> new IllegalArgumentException("Grade not found"));

        LessonPlan plan = LessonPlan.builder()
                .teacher(teacher)
                .grade(grade)
                .title(req.getTitle())
                .content(req.getContent())
                .filePath(req.getFilePath())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        LessonPlan saved = lessonPlanRepo.save(plan);

        return mapToResponse(saved);
    }

    public LessonPlanResponse mapToResponse(LessonPlan plan) {
        LessonPlanResponse dto = new LessonPlanResponse();
        dto.setId(plan.getId());
        dto.setTitle(plan.getTitle());
        dto.setContent(plan.getContent());
        dto.setFilePath(plan.getFilePath());
        dto.setCreatedAt(plan.getCreatedAt());
        dto.setUpdatedAt(plan.getUpdatedAt());
        dto.setTeacherName(plan.getTeacher().getFullName()); // giả sử có field name
        dto.setGradeName(plan.getGrade().getName());     // giả sử có field name
        return dto;
    }
}
