package com.example.demologin.repository;

import com.example.demologin.entity.Lesson;
import com.example.demologin.entity.Level;
import com.example.demologin.entity.Question;
import com.example.demologin.entity.QuestionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByType(QuestionType type);

    List<Question> findByLevelAndLessonAndType(Level level, Lesson lesson, QuestionType type);

    Page<Question> findByLevel(Level level, Pageable pageable);

    Page<Question> findByType(QuestionType type, Pageable pageable);

    @Query("""
        SELECT DISTINCT q FROM Question q
        JOIN MatrixDetail md ON q.level = md.level 
            AND q.lesson = md.lesson 
            AND q.type = md.questionType
        WHERE md.matrix.id = :matrixId
    """)
    Page<Question> findByMatrixId(@Param("matrixId") Long matrixId, Pageable pageable);

    @Query("""
        select (count(eq) > 0)
        from ExamQuestion eq
        where eq.exam.id = :examId and eq.question.id = :questionId
    """)
    boolean existsInExam(@Param("examId") Long examId,
                         @Param("questionId") Long questionId);

    @Modifying
    @Query("delete from ExamQuestion eq where eq.question.id = :questionId")
    void unlinkAllExamsOfQuestion(@Param("questionId") Long questionId);
}

