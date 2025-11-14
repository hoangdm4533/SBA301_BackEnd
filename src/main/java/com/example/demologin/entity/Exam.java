package com.example.demologin.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "exams")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String title;
    String description;
    String status;

    @Column(name = "duration_minutes")
    Integer durationMinutes;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "matrix_id")
    Matrix matrix;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL)
    List<ExamQuestion> examQuestions;

    public void addExamQuestion(ExamQuestion examQuestion) {
        examQuestions.add(examQuestion);
        examQuestion.setExam(this);
    }

    public void removeExamQuestion(ExamQuestion examQuestion) {
        examQuestions.remove(examQuestion);
        examQuestion.setExam(null);
    }
}

