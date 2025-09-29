package com.example.demologin.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ExamAttemptResponse {
    private Long id;
    private Long examTemplateId;
    private String examTitle;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Double score;
    private String status; // "IN_PROGRESS", "COMPLETED", "TIMEOUT"
    private Integer duration; // in minutes
    private Integer remainingTime; // in minutes, only for in-progress exams
}