package com.example.demologin.service;

import com.example.demologin.dto.request.exam_template.ExamTemplateRequest;
import com.example.demologin.dto.request.exam_template.AddQuestionToExamRequest;
import com.example.demologin.dto.response.ExamTemplateResponse;
import com.example.demologin.dto.response.ExamQuestionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExamTemplateService {
    ExamTemplateResponse createExamTemplate(ExamTemplateRequest request);
    ExamTemplateResponse getExamTemplateById(Long id);
    List<ExamTemplateResponse> getAllExamTemplates();
    Page<ExamTemplateResponse> getAllExamTemplates(Pageable pageable);
    Page<ExamTemplateResponse> getExamTemplatesByLevel(Long levelId, Pageable pageable);
    Page<ExamTemplateResponse> getExamTemplatesByStatus(String status, Pageable pageable);
    Page<ExamTemplateResponse> searchExamTemplates(String keyword, Pageable pageable);
    Page<ExamTemplateResponse> searchExamTemplatesByLevel(Long levelId, String keyword, Pageable pageable);
    ExamTemplateResponse updateExamTemplate(Long id, ExamTemplateRequest request);
    void deleteExamTemplate(Long id);
    
    // Question management in exam template
    ExamQuestionResponse addQuestionToExam(Long examTemplateId, AddQuestionToExamRequest request);
    void removeQuestionFromExam(Long examTemplateId, Long questionId);
    void updateQuestionInExam(Long examTemplateId, Long questionId, AddQuestionToExamRequest request);
    List<ExamQuestionResponse> getQuestionsInExam(Long examTemplateId);
    void reorderQuestionsInExam(Long examTemplateId, List<Long> questionIds);
    
    // Status management
    void publishExamTemplate(Long id);
    void archiveExamTemplate(Long id);
    void approveExamTemplate(Long id);
}