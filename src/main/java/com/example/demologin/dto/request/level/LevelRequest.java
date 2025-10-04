package com.example.demologin.dto.request.level;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Getter
@Setter
public class LevelRequest {
    
    @NotBlank(message = "Tên level không được để trống")
    private String name;
    
    private String description;
    
    private String difficulty; // EASY, MEDIUM, HARD
    
    private String status; // ACTIVE, INACTIVE
    
    @Min(value = 0, message = "Điểm tối thiểu phải >= 0")
    private Integer minScore;
    
    @Max(value = 100, message = "Điểm tối đa phải <= 100")
    private Integer maxScore;
}