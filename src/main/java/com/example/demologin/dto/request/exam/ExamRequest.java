package com.example.demologin.dto.request.exam;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class ExamRequest {
    
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;
    
    private String description;
    
    private String status; // DRAFT, PUBLISHED, ARCHIVED
}