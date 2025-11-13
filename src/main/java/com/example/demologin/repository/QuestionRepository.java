package com.example.demologin.repository;

import com.example.demologin.entity.*;
import com.example.demologin.enums.QuestionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    // Derived queries - Spring Data tự generate, không có SQL injection
    List<Question> findByType(QuestionType type);

    List<Question> findByLevelAndLessonAndType(Level level, Lesson lesson, QuestionType type);

    List<Question> findByLevelAndLessonAndTypeAndStatus(Level level, Lesson lesson, QuestionType type, QuestionStatus status);

    Page<Question> findByLevel(Level level, Pageable pageable);

    Page<Question> findByType(QuestionType type, Pageable pageable);

    Page<Question> findByStatus(QuestionStatus status, Pageable pageable);

    // Tìm câu hỏi theo ID và phải là ACTIVE
    Optional<Question> findByIdAndStatus(Long id, QuestionStatus status);

    // Query phức tạp cần thiết cho MatrixDetail (không có cách khác)
    @Query("""
        SELECT DISTINCT q FROM Question q
        JOIN MatrixDetail md ON q.level = md.level
            AND q.lesson = md.lesson
            AND q.type = md.questionType
        WHERE md.matrix.id = :matrixId
    """)
    Page<Question> findByMatrixId(@Param("matrixId") Long matrixId, Pageable pageable);

    @Query("""
        SELECT DISTINCT q FROM Question q
        JOIN MatrixDetail md ON q.level = md.level
            AND q.lesson = md.lesson
            AND q.type = md.questionType
        WHERE md.matrix.id = :matrixId
            AND q.status = com.example.demologin.enums.QuestionStatus.ACTIVE
    """)
    Page<Question> findActiveByMatrixId(@Param("matrixId") Long matrixId, Pageable pageable);

    // Query cho exam - kiểm tra question đã tồn tại trong exam chưa
    @Query("""
        select (count(eq) > 0)
        from ExamQuestion eq
        where eq.exam.id = :examId and eq.question.id = :questionId
    """)
    boolean existsInExam(@Param("examId") Long examId,
                         @Param("questionId") Long questionId);

    @Query("""
        select distinct eq.exam
        from ExamQuestion eq
        where eq.question.id = :questionId
    """)
    List<Exam> findExamsByQuestionId(@Param("questionId") Long questionId);

    @Modifying
    @Query("delete from ExamQuestion eq where eq.question.id = :questionId")
    void unlinkAllExamsOfQuestion(@Param("questionId") Long questionId);
}

