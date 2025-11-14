package com.example.demologin.service;

import com.example.demologin.enums.QuestionStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.example.demologin.dto.request.essay.EssayQuestionRequest;
import com.example.demologin.dto.response.PageResponse;
import com.example.demologin.dto.response.essay.EssayAttachmentResponse;
import com.example.demologin.dto.response.essay.EssayQuestionResponse;

public interface EssayQuestionService {
    EssayQuestionResponse createQuestion(EssayQuestionRequest request, MultipartFile[] documentFiles, MultipartFile[] imageFiles);
    
    EssayQuestionResponse updateQuestion(Long id, EssayQuestionRequest request, MultipartFile[] documentFiles, MultipartFile[] imageFiles);
    
    EssayQuestionResponse getQuestionById(Long id);
    
    EssayAttachmentResponse getAttachmentById(Long attachmentId);
    
    PageResponse<EssayQuestionResponse> getAllActiveQuestions(Pageable pageable);
    
    PageResponse<EssayQuestionResponse> getQuestionsByTeacher(Pageable pageable);
    
    PageResponse<EssayQuestionResponse> getAllQuestions(Pageable pageable);

    void changeQuestionStatus(Long id, QuestionStatus status);
    
    PageResponse<EssayQuestionResponse> searchActiveQuestions(Long gradeId, Long chapterId, Long lessonId, Pageable pageable);
}
