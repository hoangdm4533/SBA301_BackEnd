package com.example.demologin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "grades")
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "grade")
    @Builder.Default
    private List<ClassEntity> classes = new ArrayList<>();

    @OneToMany(mappedBy = "grade")
    @Builder.Default
    private List<LessonPlan> lessonPlans = new ArrayList<>();

    @OneToMany(mappedBy = "grade")
    @Builder.Default
    private List<Exam> exams = new ArrayList<>();

    @ManyToMany(mappedBy = "grades")
    @Builder.Default
    private List<Question> questions = new ArrayList<>();
}
