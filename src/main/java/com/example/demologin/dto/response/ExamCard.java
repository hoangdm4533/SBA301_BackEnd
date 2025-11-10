package com.example.demologin.dto.response;

import lombok.Data;

@Data
public class ExamCard {
    private Long id;
    private String title;
    private String description;
    private String status;         // PUBLISHED
    private Integer durationMinutes; // Thời gian làm bài (phút)
    private Integer questionCount; // số câu trong đề
}
