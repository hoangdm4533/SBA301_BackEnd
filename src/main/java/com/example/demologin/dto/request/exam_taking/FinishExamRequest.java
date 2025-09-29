package com.example.demologin.dto.request.exam_taking;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FinishExamRequest {
    @NotNull(message = "Exam attempt ID is required")
    private Long examAttemptId;

    private List<SubmitAnswerRequest> answers;
}