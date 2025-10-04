package com.example.demologin.dto.request.question;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionCreateRequest {
    private Long teacherId;                 // optional, nếu lấy từ security thì có thể bỏ
    @NotBlank
    private String questionText;
    private String type;                    // MCQ, FILL, ...
    private String difficulty;              // EASY, MEDIUM, HARD
    private String formula;                 // optional
    private List<OptionRequest> options;        // cho MCQ
    private List<Long> gradeIds;
}
