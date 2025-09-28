package com.example.demologin.repository;

import com.example.demologin.entity.ExamQuestion;
import com.example.demologin.entity.ExamTemplate;
import com.example.demologin.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Long> {
    
    List<ExamQuestion> findByExamTemplateOrderByQuestionOrder(ExamTemplate examTemplate);
    
    List<ExamQuestion> findByQuestion(Question question);
    
    @Query("SELECT eq FROM ExamQuestion eq WHERE eq.examTemplate.id = :examTemplateId ORDER BY eq.questionOrder ASC")
    List<ExamQuestion> findByExamTemplateIdOrderByQuestionOrder(@Param("examTemplateId") Long examTemplateId);
    
    Optional<ExamQuestion> findByExamTemplateAndQuestion(ExamTemplate examTemplate, Question question);
    
    Optional<ExamQuestion> findByExamTemplateAndQuestionOrder(ExamTemplate examTemplate, Integer questionOrder);
    
    boolean existsByExamTemplateAndQuestion(ExamTemplate examTemplate, Question question);
    
    boolean existsByExamTemplateAndQuestionOrder(ExamTemplate examTemplate, Integer questionOrder);
    
    long countByExamTemplate(ExamTemplate examTemplate);
    
    @Query("SELECT COALESCE(SUM(eq.points), 0) FROM ExamQuestion eq WHERE eq.examTemplate.id = :examTemplateId")
    Double sumPointsByExamTemplateId(@Param("examTemplateId") Long examTemplateId);
    
    @Query("SELECT MAX(eq.questionOrder) FROM ExamQuestion eq WHERE eq.examTemplate.id = :examTemplateId")
    Integer findMaxQuestionOrderByExamTemplateId(@Param("examTemplateId") Long examTemplateId);
}