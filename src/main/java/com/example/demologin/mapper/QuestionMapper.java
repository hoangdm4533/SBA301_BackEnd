package com.example.demologin.mapper;

import com.example.demologin.dto.request.question.OptionRequest;
import com.example.demologin.dto.response.OptionResponse;
import com.example.demologin.dto.response.QuestionResponse;
import com.example.demologin.entity.Option;
import com.example.demologin.entity.Question;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuestionMapper {
    public QuestionResponse toResponse(Question q) {
        List<OptionResponse> optionRes = (q.getOptions() == null ? List.of()
                : q.getOptions().stream()
                .map(o -> OptionResponse.builder()
                        .id(o.getId())
                        .optionText(o.getOptionText())
                        .isCorrect(o.getIsCorrect())
                        .build())
                .toList());

        return QuestionResponse.builder()
                .id(q.getId())
                .questionText(q.getQuestionText())
                .type(q.getType() != null ? q.getType().getDescription() : null)
                .formula(q.getFormula())
                .createdAt(q.getCreatedAt())
                .updatedAt(q.getUpdatedAt())
                .options(optionRes)
                .build();
    }

    public Option buildOption(Question q, OptionRequest req) {
        Option o = new Option();
        o.setQuestion(q);
        o.setOptionText(req.getOptionText());
        o.setIsCorrect(Boolean.TRUE.equals(req.getIsCorrect()));
        return o;
    }
}
