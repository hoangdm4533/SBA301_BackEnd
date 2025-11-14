package com.example.demologin.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.demologin.enums.QuestionStatus;

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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "essay_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EssayQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", nullable = false)
    Grade grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    Chapter chapter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    Lesson lesson;

    @Column(nullable = false, columnDefinition = "TEXT")
    String prompt;

    @Column(nullable = false)
    String rubric; // Tiêu chí chấm điểm

    @Column(name = "time_limit_minutes", nullable = false)
    Integer timeLimitMinutes;

    @Column(name = "max_score", nullable = false)
    Integer maxScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    QuestionStatus status = QuestionStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    User createdBy; // Teacher tạo câu hỏi

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @OneToMany(mappedBy = "essayQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<EssayAttachment> attachments = new ArrayList<>();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Helper method to maintain bidirectional relationship
    public void addAttachment(EssayAttachment attachment) {
        attachments.add(attachment);
        attachment.setEssayQuestion(this);
    }

    public void clearAttachments() {
        attachments.forEach(attachment -> attachment.setEssayQuestion(null));
        attachments.clear();
    }
}
