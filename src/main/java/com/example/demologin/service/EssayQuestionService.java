package com.example.demologin.service;

import com.example.demologin.dto.request.EssayQuestionRequest;
import com.example.demologin.dto.response.EssayQuestionResponse;
import com.example.demologin.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface EssayQuestionService {
    EssayQuestionResponse createQuestion(EssayQuestionRequest request, Long teacherId);
    
    EssayQuestionResponse updateQuestion(Long id, EssayQuestionRequest request, Long teacherId);
    
    EssayQuestionResponse getQuestionById(Long id);
    
    PageResponse<EssayQuestionResponse> getAllActiveQuestions(Pageable pageable);
    
    PageResponse<EssayQuestionResponse> getQuestionsByTeacher(Long teacherId, Pageable pageable);
    
    void deleteQuestion(Long id, Long teacherId);
    
    void archiveQuestion(Long id, Long teacherId);
}
