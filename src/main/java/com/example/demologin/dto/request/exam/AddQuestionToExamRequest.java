package com.example.demologin.dto.request.exam;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Getter
@Setter
public class AddQuestionToExamRequest {
    
    @NotNull(message = "Question ID không được để trống")
    private Long questionId;
    
    @Min(value = 0, message = "Điểm số phải >= 0")
    private Double score;
}