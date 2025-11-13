package com.example.demologin.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.demologin.enums.SubmissionStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SubmissionAttachment> attachments = new ArrayList<>();

    @Column(name = "image_urls", columnDefinition = "TEXT")
    private String imageUrls;

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

    // Helper methods for bidirectional relationship
    public void addAttachment(SubmissionAttachment attachment) {
        attachments.add(attachment);
        attachment.setSubmission(this);
    }
}
