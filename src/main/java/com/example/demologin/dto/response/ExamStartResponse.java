package com.example.demologin.dto.response;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class ExamStartResponse {
    private Long attemptId;
    private Long examId;
    private String title;
    private Integer totalQuestions;
    private Instant startedAt;
    private Instant mustSubmitBefore; // nếu có thời lượng thì set; không thì null
    private List<QuestionView> questions; // có thể trả đề để hiển thị
}
