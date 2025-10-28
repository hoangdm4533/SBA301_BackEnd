package com.example.demologin.service;

import com.example.demologin.dto.request.EssaySubmissionStartRequest;
import com.example.demologin.dto.request.EssaySubmissionSubmitRequest;
import com.example.demologin.dto.request.TeacherGradingRequest;
import com.example.demologin.dto.response.EssaySubmissionResponse;
import com.example.demologin.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface EssaySubmissionService {
    // Student operations
    EssaySubmissionResponse startEssay(EssaySubmissionStartRequest request, Long userId);
    
    EssaySubmissionResponse submitEssay(EssaySubmissionSubmitRequest request, Long userId);
    
    EssaySubmissionResponse getMySubmission(Long submissionId, Long userId);
    
    PageResponse<EssaySubmissionResponse> getMySubmissions(Long userId, Pageable pageable);
    
    // Teacher operations
    EssaySubmissionResponse gradeSubmission(TeacherGradingRequest request, Long teacherId);
    
    PageResponse<EssaySubmissionResponse> getPendingSubmissions(Pageable pageable);
    
    PageResponse<EssaySubmissionResponse> getSubmissionsForQuestion(Long questionId, Pageable pageable);
    
    EssaySubmissionResponse getSubmissionById(Long submissionId);
}
