package com.example.demologin.repository;

import com.example.demologin.entity.ExamAttempt;
import com.example.demologin.entity.Question;
import com.example.demologin.entity.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {

    List<StudentAnswer> findByExamAttempt(ExamAttempt examAttempt);

    List<StudentAnswer> findByExamAttemptOrderByQuestionId(ExamAttempt examAttempt);

    Optional<StudentAnswer> findByExamAttemptAndQuestion(ExamAttempt examAttempt, Question question);

    @Query("SELECT sa FROM StudentAnswer sa WHERE sa.examAttempt.id = :attemptId")
    List<StudentAnswer> findByAttemptId(@Param("attemptId") Long attemptId);

    @Query("SELECT COUNT(sa) FROM StudentAnswer sa WHERE sa.examAttempt.id = :attemptId AND sa.option.isCorrect = true")
    Long countCorrectAnswersByAttempt(@Param("attemptId") Long attemptId);

    void deleteByExamAttempt(ExamAttempt examAttempt);
}

