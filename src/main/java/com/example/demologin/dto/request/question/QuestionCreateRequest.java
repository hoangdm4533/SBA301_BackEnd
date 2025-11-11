package com.example.demologin.dto.request.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionCreateRequest {
    @NotBlank
    private String questionText;

    @NotBlank
    private String type;

    private String formula;

    @NotNull
    private Long lessonId;

    private Long levelId;


    @Size(min = 1, message = "At least one option is required for MCQ")
    private List<OptionRequest> options;    // MCQ_SINGLE/MCQ_MULTI/TRUE_FALSE bắt buộc
}


