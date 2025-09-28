package com.example.demologin.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class LevelResponse {
    private Long id;
    private String name;
    private String description;
    private String difficulty;
    private String status;
    private Integer minScore;
    private Integer maxScore;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer totalExamTemplates;
}