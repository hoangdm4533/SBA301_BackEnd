package com.example.demologin.dto.request.exam_taking;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitAnswerRequest {
    @NotNull(message = "Question ID is required")
    private Long questionId;

    private Long optionId; // For multiple choice questions

    private String essayAnswer; // For essay questions
}