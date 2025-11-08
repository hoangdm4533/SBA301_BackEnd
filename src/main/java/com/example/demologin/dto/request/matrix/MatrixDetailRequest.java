package com.example.demologin.dto.request.matrix;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MatrixDetailRequest {
    private Integer totalQuestions;
    private Long levelId;
    private Long lessonId;
}
