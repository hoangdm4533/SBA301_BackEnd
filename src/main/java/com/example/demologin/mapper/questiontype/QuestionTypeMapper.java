package com.example.demologin.mapper.questiontype;

import com.example.demologin.dto.response.QuestionTypeResponse;
import com.example.demologin.entity.QuestionType;
import org.springframework.stereotype.Component;

@Component
public class QuestionTypeMapper implements IQuestionTypeMapper {
    @Override
    public QuestionTypeResponse toResponse(QuestionType entity) {
        if (entity == null) {
            return null;
        }
        return QuestionTypeResponse.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .build();
    }
}

