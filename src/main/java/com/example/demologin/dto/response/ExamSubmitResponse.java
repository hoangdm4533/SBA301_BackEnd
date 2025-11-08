package com.example.demologin.dto.response;

import lombok.Data;

@Data
public class ExamSubmitResponse {
    private Long attemptId;
    private double score;
    private double maxScore;
    private int totalCorrect;
    private int totalQuestions;

    // tiện dụng: trả luôn "score/maxScore" nếu muốn
    public String getScoreDisplay() {
        // làm tròn 2 chữ số thập phân
        return String.format("%.2f/%.2f", score, maxScore);
    }

}
