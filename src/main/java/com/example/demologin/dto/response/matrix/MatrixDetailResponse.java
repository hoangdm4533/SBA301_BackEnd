package com.example.demologin.dto.response.matrix;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MatrixDetailResponse {
    private Long id;
    private Integer totalQuestions;
    private String levelDescription;
    private String questionType;
    private String lessonName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
