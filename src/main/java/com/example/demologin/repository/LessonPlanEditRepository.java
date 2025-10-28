package com.example.demologin.repository;

import com.example.demologin.entity.LessonPlan;
import com.example.demologin.entity.LessonPlanEdit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface LessonPlanEditRepository extends JpaRepository<LessonPlanEdit, Long> {
    List<LessonPlanEdit> findByLessonPlanOrderByCreatedAtAsc(LessonPlan lessonPlan);
    @Modifying
    @Transactional
    @Query("DELETE FROM LessonPlanEdit e WHERE e.lessonPlan.id = :lessonPlanId")
    void deleteByLessonPlanId(@Param("lessonPlanId") Long lessonPlanId);
    List<LessonPlanEdit> findByLessonPlanIdOrderByCreatedAtAsc(Long lessonPlanId);
}
