package com.example.demologin.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "matrix")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Matrix {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;

//    private String level; matrix detail co level-id roi
    private Integer totalQuestion;

    private Double totalScore;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "matrix")
    private List<MatrixDetail> details;
}