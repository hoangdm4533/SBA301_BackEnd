package com.example.demologin.dto.response;

import lombok.Data;

@Data
public class ExamSubmitResponse {
    private Long attemptId;
    private Double score;
    private Integer totalCorrect;
    private Integer totalQuestions;
}
