package com.example.demologin.dto.request.ai;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
public class QuestionGenerate {
    @NotBlank
    private String questionText;

    @NotBlank
    private String type;

    @NotNull
    private Long lessonId;

    private Long levelId;

    private Integer quantity;
}
