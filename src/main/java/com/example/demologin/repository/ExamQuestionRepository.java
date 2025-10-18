package com.example.demologin.repository;

import com.example.demologin.entity.ExamQuestion;
import com.example.demologin.entity.Exam;
import com.example.demologin.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Long> {
    
    // Tìm kiếm theo exam
    List<ExamQuestion> findByExam(Exam exam);
    
    // Tìm kiếm theo question
    List<ExamQuestion> findByQuestion(Question question);
    
    // Tìm kiếm theo exam và question
    Optional<ExamQuestion> findByExamAndQuestion(Exam exam, Question question);
    
    // Kiểm tra question đã có trong exam chưa
    boolean existsByExamAndQuestion(Exam exam, Question question);
    
    // Đếm số câu hỏi trong exam
    @Query("SELECT COUNT(eq) FROM ExamQuestion eq WHERE eq.exam = :exam")
    Integer countByExam(@Param("exam") Exam exam);
    
    // Tính tổng điểm của exam
    @Query("SELECT COALESCE(SUM(eq.score), 0) FROM ExamQuestion eq WHERE eq.exam = :exam")
    Double sumScoreByExam(@Param("exam") Exam exam);
    
    // Xóa tất cả questions trong exam
    void deleteByExam(Exam exam);

    boolean existsByExam_IdAndQuestion_Id(Long examId, Long questionId);
    void deleteByExam_IdAndQuestion_Id(Long examId, Long questionId);
    List<ExamQuestion> findByExam_Id(Long examId);
}