package com.example.demologin.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionResponse {
    private Long id;
    private String optionText;
    private Boolean isCorrect;
}
