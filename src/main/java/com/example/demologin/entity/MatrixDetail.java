package com.example.demologin.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "matrix_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatrixDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer totalQuestions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

//    private String status;

    @ManyToOne
    @JoinColumn(name = "level_id")
    private Level level;

    @ManyToOne
    @JoinColumn(name = "matrix_id")
    private Matrix matrix;


    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;
}
