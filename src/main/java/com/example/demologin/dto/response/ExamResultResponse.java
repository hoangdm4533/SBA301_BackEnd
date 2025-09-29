package com.example.demologin.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class ExamResultResponse {
    private Long attemptId;
    private String examTitle;
    private Double score;
    private Double maxScore;
    private Double percentage;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Long durationMinutes;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private List<QuestionResultResponse> questionResults;
}