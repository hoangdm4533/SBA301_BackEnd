package com.example.demologin.dto.response.essay;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionAttachmentResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("file_name")
    private String fileName;

    @JsonProperty("original_file_name")
    private String originalFileName;

    @JsonProperty("file_url")
    private String fileUrl;

    @JsonProperty("download_url")
    private String downloadUrl;

    @JsonProperty("file_type")
    private String fileType;

    @JsonProperty("file_size")
    private Long fileSize;

    @JsonProperty("attachment_type")
    private String attachmentType; // "IMAGE", "DOCUMENT"

    @JsonProperty("uploaded_at")
    private LocalDateTime uploadedAt;
}
