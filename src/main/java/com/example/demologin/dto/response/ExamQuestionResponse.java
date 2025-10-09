package com.example.demologin.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ExamQuestionResponse {
    private Long id;
    private Long examId;
    private Long questionId;
    private String questionText;
    private String questionType;
    private Double score;
}