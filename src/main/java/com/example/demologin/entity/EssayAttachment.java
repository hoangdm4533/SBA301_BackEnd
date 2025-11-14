package com.example.demologin.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "essay_attachments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EssayAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "essay_question_id", nullable = false)
    EssayQuestion essayQuestion;

    @Column(name = "file_name", nullable = false)
    String fileName;

    @Column(name = "original_file_name", nullable = false)
    String originalFileName;

    @Column(name = "file_url", nullable = false, length = 500)
    String fileUrl;

    @Column(name = "cloudinary_public_id", nullable = false)
    String cloudinaryPublicId;

    @Column(name = "file_type", nullable = false, length = 50)
    String fileType; // pdf, doc, docx, etc.

    @Column(name = "file_size")
    Long fileSize; // in bytes

    @Column(name = "uploaded_at", nullable = false)
    @Builder.Default
    LocalDateTime uploadedAt = LocalDateTime.now();
}
