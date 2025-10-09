//package com.example.demologin.repository;
//
//import com.example.demologin.entity.ExamAttempt;
//import com.example.demologin.entity.User;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {
//
//    List<ExamAttempt> findByStudentOrderByStartedAtDesc(User student);
//
//    Page<ExamAttempt> findByStudentOrderByStartedAtDesc(User student, Pageable pageable);
//
//    @Query("SELECT ea FROM ExamAttempt ea WHERE ea.student.userId = :studentId AND ea.finishedAt IS NULL")
//    List<ExamAttempt> findInProgressExamsByStudent(@Param("studentId") Long studentId);
//
//    @Query("SELECT ea FROM ExamAttempt ea WHERE ea.student.userId = :studentId AND ea.exam.id = :examId")
//    List<ExamAttempt> findByStudentIdAndExamId(@Param("studentId") Long studentId, @Param("examId") Long examId);
//
//    @Query("SELECT ea FROM ExamAttempt ea WHERE ea.student.userId = :studentId AND ea.examTemplate.id = :examTemplateId")
//    List<ExamAttempt> findByStudentIdAndExamTemplateId(@Param("studentId") Long studentId, @Param("examTemplateId") Long examTemplateId);
//
//    @Query("SELECT ea FROM ExamAttempt ea WHERE ea.student.userId = :studentId AND ea.finishedAt IS NOT NULL ORDER BY ea.startedAt DESC")
//    List<ExamAttempt> findCompletedExamsByStudent(@Param("studentId") Long studentId);
//
//    @Query("SELECT COUNT(ea) FROM ExamAttempt ea WHERE ea.student.userId = :studentId AND ea.exam.id = :examId")
//    Long countAttemptsByStudentAndExam(@Param("studentId") Long studentId, @Param("examId") Long examId);
//
//    @Query("SELECT COUNT(ea) FROM ExamAttempt ea WHERE ea.student.userId = :studentId AND ea.examTemplate.id = :examTemplateId")
//    Long countAttemptsByStudentAndExamTemplate(@Param("studentId") Long studentId, @Param("examTemplateId") Long examTemplateId);
//
//    Optional<ExamAttempt> findByIdAndStudent(Long id, User student);
//}
