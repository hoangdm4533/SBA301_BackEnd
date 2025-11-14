package com.example.demologin.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "exam_questions", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"exam_id", "question_id"}))
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExamQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    Exam exam;

    @ManyToOne
    @JoinColumn(name = "question_id")
    Question question;

    Double score;
}