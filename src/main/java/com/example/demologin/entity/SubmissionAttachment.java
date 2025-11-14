package com.example.demologin.entity;

import java.time.LocalDateTime;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "submission_attachments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubmissionAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    EssaySubmission submission;

    @Column(name = "file_name", nullable = false)
    String fileName;

    @Column(name = "original_file_name", nullable = false)
    String originalFileName;

    @Column(name = "file_url", nullable = false, length = 1000)
    String fileUrl;

    @Column(name = "cloudinary_public_id", nullable = false)
    String cloudinaryPublicId;

    @Column(name = "file_type", nullable = false, length = 50)
    String fileType;

    @Column(name = "file_size")
    Long fileSize;

    @Column(name = "attachment_type", nullable = false, length = 20)
    String attachmentType; // "IMAGE", "DOCUMENT"

    @CreationTimestamp
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    LocalDateTime uploadedAt;
}
