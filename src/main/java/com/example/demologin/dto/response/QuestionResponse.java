package com.example.demologin.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionResponse {
    private Long id;
    private String questionText;
    private String type;
    private String formula;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OptionResponse> options;
}
