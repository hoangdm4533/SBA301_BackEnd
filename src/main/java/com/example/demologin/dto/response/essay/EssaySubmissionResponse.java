package com.example.demologin.dto.response.essay;

import java.time.LocalDateTime;
import java.util.List;

import com.example.demologin.enums.SubmissionStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EssaySubmissionResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("student_id")
    private Long studentId;

    @JsonProperty("student_name")
    private String studentName;

    @JsonProperty("essay_question_id")
    private Long essayQuestionId;

    @JsonProperty("prompt")
    private String prompt;

    @JsonProperty("answer")
    private String answer;

    @JsonProperty("attachments")
    private List<SubmissionAttachmentResponse> attachments;

    @JsonProperty("image_urls")
    private List<String> imageUrls; // Legacy support

    @JsonProperty("started_at")
    private LocalDateTime startedAt;

    @JsonProperty("submitted_at")
    private LocalDateTime submittedAt;

    @JsonProperty("time_spent_seconds")
    private Integer timeSpentSeconds;

    @JsonProperty("time_remaining_seconds")
    private Integer timeRemainingSeconds;

    @JsonProperty("time_limit_seconds")
    private Integer timeLimitSeconds;

    @JsonProperty("score")
    private Integer score;

    @JsonProperty("max_score")
    private Integer maxScore;

    @JsonProperty("feedback")
    private String feedback;

    @JsonProperty("detailed_feedback")
    private String detailedFeedback;

    @JsonProperty("graded_by")
    private String gradedBy; // Teacher name

    @JsonProperty("status")
    private SubmissionStatus status;

    @JsonProperty("graded_at")
    private LocalDateTime gradedAt;

    @JsonProperty("is_expired")
    private Boolean isExpired;
}
