package com.example.demologin.mapper.examattempt;

import com.example.demologin.dto.response.AttemptSummary;
import com.example.demologin.dto.response.ExamStartResponse;
import com.example.demologin.dto.response.ExamSubmitResponse;
import com.example.demologin.dto.response.QuestionView;
import com.example.demologin.entity.ExamAttempt;

import java.util.List;

public interface IExamAttemptMapper {
    ExamStartResponse toStartResponse(ExamAttempt attempt,
                                      int totalQuestions,
                                      List<QuestionView> questions);

    ExamSubmitResponse toSubmitResponse(ExamAttempt attempt,
                                        double maxScore,
                                        int totalQuestions,
                                        int totalCorrect);

    AttemptSummary toMyAttemptResponse(ExamAttempt attempt,
                                       double maxScore,
                                       int totalQuestions);
}
