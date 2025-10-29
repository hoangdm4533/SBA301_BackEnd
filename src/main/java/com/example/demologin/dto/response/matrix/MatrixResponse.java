package com.example.demologin.dto.response.matrix;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class MatrixResponse {
    private Long id;
    private Integer totalQuestion;
    private Double totalScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userName;
    private String status;
    private List<MatrixDetailResponse> details;
}
