package com.example.demologin.dto.request.question;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionUpdateRequest {
    private String questionText;
    private Long typeId;  // Thay đổi từ String type sang Long typeId

    private Long lessonId;
    private Long levelId;
    private String levelCode;

    private List<OptionRequest> options;
}
