package com.example.demologin.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EssaySubmissionStartRequest {
    @NotNull(message = "Essay question ID is required")
    @JsonProperty("essay_question_id")
    private Long essayQuestionId;
}
