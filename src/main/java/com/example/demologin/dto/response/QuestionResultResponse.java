package com.example.demologin.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class QuestionResultResponse {
    private Long questionId;
    private String questionText;
    private String studentAnswer;
    private String correctAnswer;
    private Double pointsEarned;
    private Double maxPoints;
    private Boolean isCorrect;
}