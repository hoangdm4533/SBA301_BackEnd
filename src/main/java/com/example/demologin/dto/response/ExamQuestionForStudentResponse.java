package com.example.demologin.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ExamQuestionForStudentResponse {
    private Long id;
    private Long questionId;
    private String questionText;
    private String questionType; // "MULTIPLE_CHOICE", "ESSAY", etc.
    private Integer questionOrder;
    private Double points;
    private List<OptionForStudentResponse> options; // Only for multiple choice
}