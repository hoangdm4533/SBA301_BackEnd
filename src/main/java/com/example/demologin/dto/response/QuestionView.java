package com.example.demologin.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class QuestionView {
    private Long id;
    private String text;
    private String questionType;
    private List<OptionView> options;
    private Double score;
}
