package com.example.demologin.dto.request.exam;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AnswerPayload {
    @NotNull
    private Long questionId;

    // với MCQ/TF: truyền danh sách optionId đã chọn (1 hoặc nhiều)
    private List<Long> selectedOptionIds;

    //tự luận
    private String answerText;
}
