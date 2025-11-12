package com.example.demologin.dto.request.questiontype;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionTypeCreateRequest {
    @NotBlank(message = "Description is required")
    private String description;
}
