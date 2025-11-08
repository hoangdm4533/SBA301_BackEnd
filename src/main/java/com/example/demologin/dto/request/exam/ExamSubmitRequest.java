package com.example.demologin.dto.request.exam;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ExamSubmitRequest {
    @NotNull
    private List<AnswerPayload> answers;
}
