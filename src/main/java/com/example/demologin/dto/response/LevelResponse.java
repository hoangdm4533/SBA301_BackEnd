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
    private String difficulty;
    private String status;
    private Double score;
}