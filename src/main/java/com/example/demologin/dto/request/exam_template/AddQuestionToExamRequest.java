package com.example.demologin.dto.request.exam_template;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Getter
@Setter
public class AddQuestionToExamRequest {
    
    @NotNull(message = "Question ID không được để trống")
    private Long questionId;
    
    @NotNull(message = "Thứ tự câu hỏi không được để trống")
    @Min(value = 1, message = "Thứ tự câu hỏi phải >= 1")
    private Integer questionOrder;
    
    @Min(value = 0, message = "Điểm phải >= 0")
    private Double points;
    
    private String note;
}