package com.example.demologin.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ExamQuestionResponse {
    private Long id;
    private Long examTemplateId;
    private Long questionId;
    private String questionText;
    private String questionType;
    private String questionDifficulty;
    private Integer questionOrder;
    private Double points;
    private String note;
    private LocalDateTime createdAt;
}