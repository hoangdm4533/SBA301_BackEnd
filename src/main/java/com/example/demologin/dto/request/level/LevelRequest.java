package com.example.demologin.dto.request.level;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LevelRequest {
    

    @NotBlank(message = "Tên level không được để trống")
    private String description;
    
    private String difficulty; // EASY, MEDIUM, HARD
    
    private String status; // ACTIVE, INACTIVE

    @DecimalMin(value = "0", inclusive = false, message = "Giá trị phải > 0")
    @DecimalMax(value = "10", inclusive = false, message = "Giá trị phải < 10")
    private Double score;
}