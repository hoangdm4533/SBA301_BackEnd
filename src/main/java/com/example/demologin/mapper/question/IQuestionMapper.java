package com.example.demologin.mapper.question;

import com.example.demologin.dto.request.question.OptionRequest;
import com.example.demologin.dto.response.QuestionResponse;
import com.example.demologin.entity.Option;
import com.example.demologin.entity.Question;

public interface IQuestionMapper {
    QuestionResponse toResponse(Question q);
    Option buildOption(Question q, OptionRequest req);

}
