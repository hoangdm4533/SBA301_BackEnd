package com.example.demologin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demologin.entity.EssayQuestion;
import com.example.demologin.enums.QuestionStatus;

@Repository
public interface EssayQuestionRepository extends JpaRepository<EssayQuestion, Long> {
    Page<EssayQuestion> findByStatus(QuestionStatus status, Pageable pageable);

    Page<EssayQuestion> findByCreatedByUserIdOrderByCreatedAtDesc(Long teacherId, Pageable pageable);
    
    Page<EssayQuestion> findByStatusAndGradeIdAndChapterIdAndLessonId(
        QuestionStatus status, Long gradeId, Long chapterId, Long lessonId, Pageable pageable
    );
    
    Page<EssayQuestion> findByStatusAndGradeId(QuestionStatus status, Long gradeId, Pageable pageable);
    
    Page<EssayQuestion> findByStatusAndGradeIdAndChapterId(
        QuestionStatus status, Long gradeId, Long chapterId, Pageable pageable
    );
}
