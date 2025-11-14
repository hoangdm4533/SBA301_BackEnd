package com.example.demologin.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "grades")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Integer gradeNumber;

    String description;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    Subject subject;

    @OneToMany(mappedBy = "grade")
    List<LessonPlan> lessonPlans;

    @OneToMany(mappedBy = "grade")
    List<Chapter> chapters;

}
