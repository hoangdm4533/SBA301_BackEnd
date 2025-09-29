package com.example.demologin.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OptionForStudentResponse {
    private Long id;
    private String optionText;
    // Note: We don't include isCorrect for security reasons
}