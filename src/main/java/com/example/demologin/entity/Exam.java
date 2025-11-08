package com.example.demologin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "exams")
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "matrix_id")
    private Matrix matrix;

    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL)
    private List<ExamQuestion> examQuestions;

    public void addExamQuestion(ExamQuestion examQuestion) {
        examQuestions.add(examQuestion);
        examQuestion.setExam(this);
    }

    public void removeExamQuestion(ExamQuestion examQuestion) {
        examQuestions.remove(examQuestion);
        examQuestion.setExam(null);
    }
}

