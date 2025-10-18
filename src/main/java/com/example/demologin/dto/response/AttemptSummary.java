package com.example.demologin.dto.response;

import lombok.Data;

import java.time.Instant;

@Data
public class AttemptSummary {
    private Long attemptId;
    private Long examId;
    private String examTitle;
    private Double score;
    private String status;      // IN_PROGRESS | GRADED
    private Instant submittedAt;
}
