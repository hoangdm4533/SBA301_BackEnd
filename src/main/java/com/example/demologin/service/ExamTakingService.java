package com.example.demologin.service;

import com.example.demologin.dto.request.exam_taking.FinishExamRequest;
import com.example.demologin.dto.request.exam_taking.StartExamRequest;
import com.example.demologin.dto.request.exam_taking.SubmitAnswerRequest;
import com.example.demologin.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExamTakingService {

    // Get available exams for students
    Page<AvailableExamResponse> getAvailableExams(Pageable pageable);
    Page<AvailableExamResponse> getAvailableExamsByDifficulty(String difficulty, Pageable pageable);
    Page<AvailableExamResponse> getAvailableExamsByLevel(Long levelId, Pageable pageable);

    // Start an exam
    ExamAttemptResponse startExam(StartExamRequest request);

    // Get exam questions for current attempt
    List<ExamQuestionForStudentResponse> getExamQuestions(Long examAttemptId);

    // Submit answer to a question
    void submitAnswer(Long examAttemptId, SubmitAnswerRequest request);

    // Get current exam attempt details
    ExamAttemptResponse getCurrentExamAttempt(Long examAttemptId);

    // Finish exam and calculate score
    ExamResultResponse finishExam(FinishExamRequest request);

    // Get exam results
    ExamResultResponse getExamResult(Long examAttemptId);

    // Get student's exam history
    Page<ExamAttemptResponse> getMyExamHistory(Pageable pageable);

    // Get in-progress exams
    List<ExamAttemptResponse> getInProgressExams();

    // Debug method to check data state
    Object debugCheckData();

    // Cleanup inconsistent exam states
    Object cleanupInconsistentState();
}