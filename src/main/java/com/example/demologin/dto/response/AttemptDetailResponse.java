package com.example.demologin.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO chứa chi tiết của một bài thi đã làm
 */
@Data
public class AttemptDetailResponse {
    private Long attemptId;
    private Long examId;
    private String examTitle;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private double score;
    private double maxScore;
    private int totalQuestions;
    private int correctAnswers;
    private List<AttemptQuestionDetail> questions;

    @Data
    public static class AttemptQuestionDetail {
        private Long questionId;
        private String questionText;
        private String questionType;
        private double score;
        private double maxScore;
        private boolean isCorrect;
        private List<OptionDetail> options;
        private List<Long> selectedOptionIds;
        private List<Long> correctOptionIds;
    }

    @Data
    public static class OptionDetail {
        private Long id;
        private String text;
        private Boolean isCorrect;
        private Boolean isSelected;
    }
}

