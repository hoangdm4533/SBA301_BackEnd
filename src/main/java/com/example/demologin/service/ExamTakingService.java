package com.example.demologin.service;

import com.example.demologin.dto.request.exam.ExamSubmitRequest;
import com.example.demologin.dto.response.*;
import org.springframework.data.domain.Page;

public interface ExamTakingService {

    Page<ExamCard> listAvailable(int page, int size);
    ExamStartResponse startAttempt(Long examId);
    ExamSubmitResponse submitAttempt(Long attemptId, ExamSubmitRequest req);
    Page<AttemptSummary> myAttempts(int page, int size);

}