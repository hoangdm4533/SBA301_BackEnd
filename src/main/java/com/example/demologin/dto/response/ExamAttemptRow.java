package com.example.demologin.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExamAttemptRow {
    private Long attemptId;
    private Long examId;
    private String examTitle;
    private Long studentId;
    private String studentName;
    private String studentUsername;
    private String studentEmail;
    private Double score;
    private String gradedBy;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private String status;
}
