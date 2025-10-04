package com.example.demologin.dto.request.question;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionUpdateRequest {
    private String questionText;
    private String type;
    private String difficulty;
    private String formula;

    // Nếu gửi null => không đổi; nếu gửi list => replace toàn bộ
    private List<OptionRequest> options;
    private List<Long> gradeIds;
}
