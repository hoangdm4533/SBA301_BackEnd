package com.example.demologin.service;

import com.example.demologin.dto.request.essay.EssaySubmissionStartRequest;
import com.example.demologin.dto.request.TeacherGradingRequest;
import com.example.demologin.dto.response.essay.EssaySubmissionResponse;
import com.example.demologin.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface EssaySubmissionService {
    // Student operations
    EssaySubmissionResponse startEssay(EssaySubmissionStartRequest request);

    EssaySubmissionResponse submitEssayWithFiles(
        Long submissionId, 
        String answer, 
        MultipartFile[] imageFiles,
        MultipartFile[] documentFiles);
    
    EssaySubmissionResponse getMySubmission(Long submissionId);
    
    PageResponse<EssaySubmissionResponse> getMySubmissions(Pageable pageable);
    
    // Teacher operations
    EssaySubmissionResponse gradeSubmission(TeacherGradingRequest request);
    
    PageResponse<EssaySubmissionResponse> getPendingSubmissions(Pageable pageable);
    
    PageResponse<EssaySubmissionResponse> getPendingSubmissionsForTeacher(Long teacherId, Pageable pageable);
    
    PageResponse<EssaySubmissionResponse> getSubmissionsForQuestion(Long questionId, Pageable pageable);
    
    EssaySubmissionResponse getSubmissionById(Long submissionId);
}
