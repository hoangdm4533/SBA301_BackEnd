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
@Table(name = "student_answers")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String essayAnswer;
    Double score;
    LocalDateTime gradedAt;

    @ManyToOne
    @JoinColumn(name = "exam_attemp_id")
    ExamAttempt examAttempt;

    @ManyToOne
    @JoinColumn(name = "question_id")
    Question question;

    @ManyToOne
    @JoinColumn(name = "option_id")
    Option option;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
}
