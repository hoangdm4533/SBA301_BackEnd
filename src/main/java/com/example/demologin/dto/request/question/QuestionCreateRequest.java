package com.example.demologin.dto.request.question;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionCreateRequest {
    @NotBlank
    private String questionText;
    private String type;                 // ví dụ: "MCQ", "ESSAY" (map sang QuestionType.description)
    private String formula;
    private List<OptionRequest> options; // cho MCQ
}
