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
public class EssayQuestionRequest {
    @NotBlank(message = "Prompt is required")
    @JsonProperty("prompt")
    private String prompt;

    @NotBlank(message = "Rubric is required")
    @JsonProperty("rubric")
    private String rubric;

    @NotNull(message = "Time limit is required")
    @Min(value = 1, message = "Time limit must be at least 1 minute")
    @Max(value = 180, message = "Time limit cannot exceed 180 minutes")
    @JsonProperty("time_limit_minutes")
    private Integer timeLimitMinutes;

    @NotNull(message = "Max score is required")
    @Min(value = 1, message = "Max score must be at least 1")
    @Max(value = 100, message = "Max score cannot exceed 100")
    @JsonProperty("max_score")
    private Integer maxScore;
}
