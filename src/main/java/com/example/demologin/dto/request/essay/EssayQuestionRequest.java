package com.example.demologin.dto.request.essay;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EssayQuestionRequest {
    @NotNull(message = "Grade ID is required")
    @JsonProperty("grade_id")
    private Long gradeId;

    @NotNull(message = "Chapter ID is required")
    @JsonProperty("chapter_id")
    private Long chapterId;

    @NotNull(message = "Lesson ID is required")
    @JsonProperty("lesson_id")
    private Long lessonId;

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
