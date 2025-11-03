package com.example.demologin.mapper.examattempt;

import com.example.demologin.dto.response.*;
import com.example.demologin.entity.Exam;
import com.example.demologin.entity.ExamAttempt;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.List;

@Component
public class ExamAttemptMapper implements IExamAttemptMapper{

    @Override
    public ExamStartResponse toStartResponse(ExamAttempt attempt,
                                             int totalQuestions,
                                             List<QuestionView> questions) {
        if (attempt == null) return null;

        Exam exam = attempt.getExam();
        ExamStartResponse resp = new ExamStartResponse();
        resp.setAttemptId(attempt.getId());
        resp.setExamId(exam != null ? exam.getId() : null);
        resp.setTitle(exam != null ? exam.getTitle() : null);
        resp.setTotalQuestions(totalQuestions);
        resp.setStartedAt(attempt.getStartedAt() == null
                ? null
                : attempt.getStartedAt().atZone(ZoneId.systemDefault()).toInstant());
        resp.setMustSubmitBefore(null); // giữ nguyên như code gốc
        resp.setQuestions(questions);
        return resp;
    }

    @Override
    public ExamSubmitResponse toSubmitResponse(ExamAttempt attempt,
                                               double maxScore,
                                               int totalQuestions,
                                               int totalCorrect) {
        if (attempt == null) return null;

        ExamSubmitResponse resp = new ExamSubmitResponse();
        resp.setAttemptId(attempt.getId());
        resp.setScore(attempt.getScore());
        resp.setMaxScore(maxScore);
        resp.setTotalCorrect(totalCorrect);
        resp.setTotalQuestions(totalQuestions);
        return resp;
    }

    @Override
    public AttemptSummary toMyAttemptResponse(ExamAttempt attempt,
                                              double maxScore,
                                              int totalQuestions) {
        if (attempt == null) return null;

        AttemptSummary resp = new AttemptSummary();
        resp.setAttemptId(attempt.getId());
        resp.setExamId(attempt.getExam() != null ? attempt.getExam().getId() : null);
        resp.setTitle(attempt.getExam() != null ? attempt.getExam().getTitle() : null);
        resp.setScore(attempt.getScore() == null ? 0.0 : attempt.getScore());
        resp.setMaxScore(maxScore);
        resp.setTotalQuestions(totalQuestions);
        resp.setStartedAt(attempt.getStartedAt() == null
                ? null
                : attempt.getStartedAt().atZone(ZoneId.systemDefault()).toInstant());
        resp.setFinishedAt(attempt.getFinishedAt() == null
                ? null
                : attempt.getFinishedAt().atZone(ZoneId.systemDefault()).toInstant());
        return resp;
    }
}

