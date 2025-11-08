package com.example.demologin.dto.request.exam_taking;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StartExamRequest {
    @NotNull(message = "Exam ID is required")
    private Long examId;
}