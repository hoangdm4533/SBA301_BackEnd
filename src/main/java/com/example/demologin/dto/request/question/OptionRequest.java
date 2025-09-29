package com.example.demologin.dto.request.question;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OptionRequest {
    @NotBlank
    private String optionText;
    private Boolean isCorrect;

}
