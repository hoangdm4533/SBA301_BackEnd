package com.example.demologin.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "lesson_plan_edits")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonPlanEdit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "lesson_plan_id", nullable = false)
    LessonPlan lessonPlan;

    @ManyToOne
    @JoinColumn(name = "editor_id")
    User editor;

    @Column(columnDefinition = "JSON", nullable = false)
    String operation;

    LocalDateTime createdAt = LocalDateTime.now();
}
