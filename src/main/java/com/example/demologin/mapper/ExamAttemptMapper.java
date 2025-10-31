package com.example.demologin.mapper;

import com.example.demologin.dto.response.*;
import com.example.demologin.entity.Exam;
import com.example.demologin.entity.ExamAttempt;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.List;

@Component
public class ExamAttemptMapper {

    public ExamStartResponse toStartResponse(ExamAttempt attempt,
                                             int totalQuestions,
                                             List<QuestionView> questions) {
        Exam exam = attempt.getExam();
        ExamStartResponse resp = new ExamStartResponse();
        resp.setAttemptId(attempt.getId());
        resp.setExamId(exam.getId());
        resp.setTitle(exam.getTitle());
        resp.setTotalQuestions(totalQuestions);
        resp.setStartedAt(attempt.getStartedAt().atZone(ZoneId.systemDefault()).toInstant());
        resp.setMustSubmitBefore(null);
        resp.setQuestions(questions);
        return resp;
    }
    public ExamSubmitResponse toSubmitResponse(ExamAttempt attempt, double maxScore, int totalQuestions, int totalCorrect) {
        ExamSubmitResponse resp = new ExamSubmitResponse();
        resp.setAttemptId(attempt.getId());
        resp.setScore(attempt.getScore());
        resp.setMaxScore(maxScore);
        resp.setTotalCorrect(totalCorrect);
        resp.setTotalQuestions(totalQuestions);
        return resp;
    }

    public AttemptSummary toMyAttemptResponse(ExamAttempt attempt, double maxScore, int totalQuestions) {
        AttemptSummary resp = new AttemptSummary();
        resp.setAttemptId(attempt.getId());
        resp.setExamId(attempt.getExam().getId());
        resp.setTitle(attempt.getExam().getTitle());
        resp.setScore(attempt.getScore() == null ? 0.0 : attempt.getScore());
        resp.setMaxScore(maxScore);
        resp.setTotalQuestions(totalQuestions);
        resp.setStartedAt(attempt.getStartedAt() == null ? null
                : attempt.getStartedAt().atZone(ZoneId.systemDefault()).toInstant());
        resp.setFinishedAt(attempt.getFinishedAt() == null ? null
                : attempt.getFinishedAt().atZone(ZoneId.systemDefault()).toInstant());
        return resp;
    }
}

