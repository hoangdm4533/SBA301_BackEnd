package com.example.demologin.dto.response;

import com.example.demologin.enums.QuestionStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EssayQuestionResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("prompt")
    private String prompt;

    @JsonProperty("rubric")
    private String rubric;

    @JsonProperty("time_limit_minutes")
    private Integer timeLimitMinutes;

    @JsonProperty("max_score")
    private Integer maxScore;

    @JsonProperty("status")
    private QuestionStatus status;

    @JsonProperty("created_by")
    private String createdBy; // Teacher name

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
