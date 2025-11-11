package com.example.demologin.service;

import com.example.demologin.dto.request.question.QuestionCreateRequest;
import com.example.demologin.dto.request.question.QuestionUpdateRequest;
import com.example.demologin.dto.response.QuestionResponse;
import org.springframework.data.domain.Page;


public interface QuestionService {
    Page<QuestionResponse> list(int page, int size);
    QuestionResponse get(Long id);
    QuestionResponse create(QuestionCreateRequest req);
    QuestionResponse update(Long id, QuestionUpdateRequest req);
    void delete(Long id);
    String generateQuestion(QuestionCreateRequest req);
}
