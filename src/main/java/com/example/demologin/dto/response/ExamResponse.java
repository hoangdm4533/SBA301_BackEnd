package com.example.demologin.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class ExamResponse {
    private Long id;
    private String title;
    private String description;
    private String status;
    private Integer durationMinutes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long matrixId;
    private List<ExamQuestionResponse> questions;
}