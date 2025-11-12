package com.example.demologin.mapper.questiontype;

import com.example.demologin.dto.response.QuestionTypeResponse;
import com.example.demologin.entity.QuestionType;

public interface IQuestionTypeMapper {
    QuestionTypeResponse toResponse(QuestionType entity);
}
