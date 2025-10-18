package com.example.demologin.dto.request.exam;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class ExamCreateRequest {

    @NotBlank
    private String title;
    private String description;
    private List<Long> questionIds;   // ids câu hỏi
    private Double defaultScore;      // mặc định 1.0
}
