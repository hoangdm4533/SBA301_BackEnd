package com.example.demologin.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExamQuestionResponse {
    private Long id;
    private Long examId;
    private Long questionId;
    private String questionText;
    private String questionType;
    private double score;
}