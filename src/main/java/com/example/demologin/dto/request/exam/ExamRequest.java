package com.example.demologin.dto.request.exam;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class ExamRequest {
    
    @NotBlank(message = "Tiêu đề không được để trống")
    private String title;

    @JsonProperty("matrix_id")
    private Long matrixId;
    
    private String description;
    
    private String status; // DRAFT, PUBLISHED, ARCHIVED

    @JsonProperty("duration_minutes")
    private Integer durationMinutes;
}