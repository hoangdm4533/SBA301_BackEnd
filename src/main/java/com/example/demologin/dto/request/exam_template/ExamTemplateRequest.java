package com.example.demologin.dto.request.exam_template;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Getter
@Setter
public class ExamTemplateRequest {
    
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;
    
    private String description;
    
    @NotNull(message = "Level ID không được để trống")
    private Long levelId;
    
    private String difficulty; // EASY, MEDIUM, HARD
    
    @Min(value = 1, message = "Thời gian làm bài phải >= 1 phút")
    private Integer duration;
    
    @Min(value = 1, message = "Tổng số câu hỏi phải >= 1")
    private Integer totalQuestions;
    
    @Min(value = 0, message = "Tổng điểm phải >= 0")
    private Double totalPoints;
}