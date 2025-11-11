package com.example.demologin.dto.request.exam;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AnswerPayload {
    @NotNull
    private Long questionId;

    // Truyền danh sách optionId đã chọn (1 hoặc nhiều) cho câu hỏi trắc nghiệm
    private List<Long> selectedOptionIds;
}
