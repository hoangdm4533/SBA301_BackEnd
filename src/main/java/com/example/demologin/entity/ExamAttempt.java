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
@Table(name = "exam_attempts")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExamAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    LocalDateTime startedAt;
    LocalDateTime finishedAt;

    @Column(name = "expires_at")
    LocalDateTime expiresAt;

    Double score;
    String gradedBy;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    Exam exam;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @OneToMany(mappedBy = "examAttempt")
    List<StudentAnswer> studentAnswers;
}

