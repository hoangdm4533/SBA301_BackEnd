package com.example.demologin.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AvailableExamResponse {
    private Long id;
    private String title;
    private String description;
    private String difficulty;
    private Integer duration; // in minutes
    private Integer totalQuestions;
    private Double totalPoints;
    private String levelName;
    private Long levelId;
}