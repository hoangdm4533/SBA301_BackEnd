package com.example.demologin.service;

import com.example.demologin.dto.request.ai.QuestionGenerate;
import com.example.demologin.dto.request.question.QuestionCreateRequest;
import com.example.demologin.dto.request.question.QuestionUpdateRequest;
import com.example.demologin.dto.response.QuestionResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


public interface QuestionService {
    Page<QuestionResponse> list(int page, int size);
    QuestionResponse get(Long id);
    QuestionResponse create(QuestionCreateRequest req);
    QuestionResponse update(Long id, QuestionUpdateRequest req);
    void delete(Long id);
    String generateQuestion(QuestionGenerate req) throws InterruptedException, ExecutionException, TimeoutException;
    Page<QuestionResponse> listByLevel(Long levelId, int page, int size);
    Page<QuestionResponse> listByType(Long typeId, int page, int size);
    Page<QuestionResponse> listByMatrix(Long matrixId, int page, int size);
    Page<QuestionResponse> listByStatus(String status, int page, int size);
    QuestionResponse changeStatus(Long id);
    List<QuestionResponse> saveQuestionsAI(List<QuestionCreateRequest> requests);
}
