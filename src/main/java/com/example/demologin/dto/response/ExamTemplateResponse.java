package com.example.demologin.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class ExamTemplateResponse {
    private Long id;
    private String title;
    private String description;
    private Long levelId;
    private String levelName;
    private String difficulty;
    private String status;
    private Integer duration;
    private Integer totalQuestions;
    private Double totalPoints;
    private String createdBy;
    private String updatedBy;
    private String approvedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime approvedAt;
    private List<ExamQuestionResponse> questions;
}