package com.example.demologin.dto.response;

import lombok.Data;

import java.time.Instant;

@Data
public class AttemptSummary {
    private Long attemptId;
    private Long examId;
    private String title;
    private double score;      // điểm đạt
    private double maxScore;   // điểm tối đa
    private int totalQuestions;
    private Instant startedAt;
    private Instant finishedAt;
    public String getScoreDisplay() {
        return String.format("%.2f/%.2f", score, maxScore);
    }
}
