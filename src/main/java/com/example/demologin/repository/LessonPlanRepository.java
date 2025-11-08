package com.example.demologin.repository;

import com.example.demologin.entity.LessonPlan;
import com.example.demologin.entity.LessonPlanEdit;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LessonPlanRepository extends JpaRepository<LessonPlan, Long> {
    @NotNull Optional<LessonPlan> findById(Long id);
}
