package com.example.demologin.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherGradingRequest {
    @NotNull(message = "Submission ID is required")
    @JsonProperty("submission_id")
    private Long submissionId;

    @NotNull(message = "Score is required")
    @Min(value = 0, message = "Score cannot be negative")
    @JsonProperty("score")
    private Integer score;

    @NotBlank(message = "Feedback is required")
    @JsonProperty("feedback")
    private String feedback;

    @JsonProperty("detailed_feedback")
    private String detailedFeedback;
}
