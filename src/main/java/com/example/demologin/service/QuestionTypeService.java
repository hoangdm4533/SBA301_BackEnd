package com.example.demologin.service;

import com.example.demologin.dto.request.questiontype.QuestionTypeCreateRequest;
import com.example.demologin.dto.request.questiontype.QuestionTypeUpdateRequest;
import com.example.demologin.dto.response.QuestionTypeResponse;
import org.springframework.data.domain.Page;

public interface QuestionTypeService {
    Page<QuestionTypeResponse> list(int page, int size);
    QuestionTypeResponse get(Long id);
    QuestionTypeResponse create(QuestionTypeCreateRequest req);
    QuestionTypeResponse update(Long id, QuestionTypeUpdateRequest req);
    void delete(Long id);
    QuestionTypeResponse findByDescription(String description);
}
