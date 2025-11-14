package com.example.demologin.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "matrix_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MatrixDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Integer totalQuestions;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "level_id")
    Level level;

    @ManyToOne
    @JoinColumn(name = "question_type_id")
    QuestionType questionType;

    @ManyToOne
    @JoinColumn(name = "matrix_id")
    Matrix matrix;


    @ManyToOne
    @JoinColumn(name = "lesson_id")
    Lesson lesson;
}
