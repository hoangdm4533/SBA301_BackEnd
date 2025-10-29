package com.example.demologin.dto.request.matrix;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
@Getter
@Builder
public class MatrixRequest {
    private Integer totalQuestion;
    private Double totalScore;
    private Long userId;
    private List<MatrixDetailRequest> details;
}
