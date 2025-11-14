package com.example.demologin.repository;

import com.example.demologin.entity.ExamAttempt;
import com.example.demologin.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {

    @Query("""
  SELECT a FROM ExamAttempt a
  JOIN a.user u
  JOIN a.exam e
  WHERE e.id = :examId
    AND (
      :kw IS NULL OR
      LOWER(u.fullName) LIKE LOWER(CONCAT('%', :kw, '%')) OR
      LOWER(u.username) LIKE LOWER(CONCAT('%', :kw, '%')) OR
      LOWER(u.email) LIKE LOWER(CONCAT('%', :kw, '%'))
    )
    AND (
      :from IS NULL OR a.finishedAt IS NULL OR a.finishedAt >= :from
    )
    AND (
      :to IS NULL OR a.finishedAt IS NULL OR a.finishedAt < :to
    )
""")
    Page<ExamAttempt> searchForTeacher(
            @Param("examId") Long examId,
            @Param("kw") String keyword,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable);

    Page<ExamAttempt> findByUser_UserId(Long userId, Pageable pageable);

    List<ExamAttempt> findByExpiresAtBeforeAndFinishedAtIsNull(LocalDateTime expiresAt);
}
