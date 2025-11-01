package com.example.demologin.entity;

import com.example.demologin.enums.SubmissionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "essay_submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EssaySubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Student làm bài

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "essay_question_id", nullable = false)
    private EssayQuestion essayQuestion;

    @Column(columnDefinition = "TEXT")
    private String answer; // Câu trả lời của student

    @Column(name = "image_urls", columnDefinition = "TEXT")
    private String imageUrls; // JSON array: ["url1", "url2", ...]

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "time_spent_seconds")
    private Integer timeSpentSeconds;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SubmissionStatus status = SubmissionStatus.ONGOING;

    // Teacher grading fields
    @Column
    private Integer score;

    @Column(columnDefinition = "TEXT")
    private String feedback; // Nhận xét tổng quan

    @Column(name = "detailed_feedback", columnDefinition = "TEXT")
    private String detailedFeedback; // Nhận xét chi tiết

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graded_by")
    private User gradedBy; // Teacher chấm bài

    @Column(name = "graded_at")
    private LocalDateTime gradedAt;
}
