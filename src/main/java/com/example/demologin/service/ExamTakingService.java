package com.example.demologin.service;

import com.example.demologin.dto.request.exam.ExamSubmitRequest;
import com.example.demologin.dto.request.exam_taking.FinishExamRequest;
import com.example.demologin.dto.request.exam_taking.StartExamRequest;
import com.example.demologin.dto.request.exam_taking.SubmitAnswerRequest;
import com.example.demologin.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExamTakingService {

    Page<ExamCard> listAvailable(int page, int size);
    ExamStartResponse startAttempt(Long examId);
    ExamSubmitResponse submitAttempt(Long attemptId, ExamSubmitRequest req);
    Page<AttemptSummary> myAttempts(int page, int size);

}