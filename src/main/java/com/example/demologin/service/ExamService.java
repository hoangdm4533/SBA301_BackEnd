package com.example.demologin.service;

import com.example.demologin.dto.request.exam.ExamRequest;
import com.example.demologin.dto.request.exam.AddQuestionToExamRequest;
import com.example.demologin.dto.response.ExamResponse;
import com.example.demologin.dto.response.ExamQuestionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExamService {
    
    // CRUD cơ bản
    ExamResponse createExam(ExamRequest request);
    ExamResponse getExamById(Long id);
    List<ExamResponse> getAllExams();
    Page<ExamResponse> getAllExams(int page, int size, String sortBy, String sortDir);
    ExamResponse updateExam(Long id, ExamRequest request);
    void deleteExam(Long id);
    
    // Tìm kiếm và lọc
    Page<ExamResponse> getExamsByStatus(String status, int page, int size, String sortBy, String sortDir);
    Page<ExamResponse> searchExams(String keyword, Pageable pageable);
    
    // Quản lý câu hỏi trong exam
    ExamQuestionResponse addQuestionToExam(Long examId, AddQuestionToExamRequest request);
    void removeQuestionFromExam(Long examId, Long questionId);
    List<ExamQuestionResponse> getQuestionsInExam(Long examId);
    
    // Quản lý trạng thái
    void publishExam(Long id);
    void archiveExam(Long id);
    
    // Exam cho học sinh
    Page<ExamResponse> getPublishedExams(Pageable pageable);
}