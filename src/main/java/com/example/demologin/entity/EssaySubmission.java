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
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "essay_submissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EssaySubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user; // Student làm bài

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "essay_question_id", nullable = false)
    EssayQuestion essayQuestion;

    @Column(columnDefinition = "TEXT")
    String answer; // Câu trả lời của student

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<SubmissionAttachment> attachments = new ArrayList<>();

    @Column(name = "image_urls", columnDefinition = "TEXT")
    String imageUrls;

    @Column(name = "started_at", nullable = false)
    LocalDateTime startedAt;

    @Column(name = "submitted_at")
    LocalDateTime submittedAt;

    @Column(name = "time_spent_seconds")
    Integer timeSpentSeconds;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    SubmissionStatus status = SubmissionStatus.ONGOING;

    // Teacher grading fields
    @Column
    Integer score;

    @Column(columnDefinition = "TEXT")
    String feedback; // Nhận xét tổng quan

    @Column(name = "detailed_feedback", columnDefinition = "TEXT")
    String detailedFeedback; // Nhận xét chi tiết

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graded_by")
    User gradedBy; // Teacher chấm bài

    @Column(name = "graded_at")
    LocalDateTime gradedAt;

    // Helper methods for bidirectional relationship
    public void addAttachment(SubmissionAttachment attachment) {
        attachments.add(attachment);
        attachment.setSubmission(this);
    }
}
