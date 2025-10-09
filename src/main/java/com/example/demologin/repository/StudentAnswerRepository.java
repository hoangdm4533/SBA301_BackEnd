//package com.example.demologin.repository;
//
//import com.example.demologin.entity.ExamAttempt;
//import com.example.demologin.entity.Question;
//import com.example.demologin.entity.StudentAnswer;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {
//
//    List<StudentAnswer> findByAttempt(ExamAttempt attempt);
//
//    List<StudentAnswer> findByAttemptOrderByQuestionId(ExamAttempt attempt);
//
//    Optional<StudentAnswer> findByAttemptAndQuestion(ExamAttempt attempt, Question question);
//
//    @Query("SELECT sa FROM StudentAnswer sa WHERE sa.attempt.id = :attemptId")
//    List<StudentAnswer> findByAttemptId(@Param("attemptId") Long attemptId);
//
//    @Query("SELECT COUNT(sa) FROM StudentAnswer sa WHERE sa.attempt.id = :attemptId AND sa.option.isCorrect = true")
//    Long countCorrectAnswersByAttempt(@Param("attemptId") Long attemptId);
//
//    void deleteByAttempt(ExamAttempt attempt);
//}
