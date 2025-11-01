package com.example.demologin.repository;

import com.example.demologin.entity.EssaySubmission;
import com.example.demologin.enums.SubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EssaySubmissionRepository extends JpaRepository<EssaySubmission, Long> {
    // Find student's submission for a specific question
    Optional<EssaySubmission> findByUserUserIdAndEssayQuestionId(Long userId, Long questionId);
    
    // Find all submissions by student
    Page<EssaySubmission> findByUserUserIdOrderByStartedAtDesc(Long userId, Pageable pageable);
    
    // Find all submissions for a question (for teacher)
    Page<EssaySubmission> findByEssayQuestionIdOrderBySubmittedAtDesc(Long questionId, Pageable pageable);
    
    // Find submissions by status
    Page<EssaySubmission> findByStatusOrderBySubmittedAtDesc(SubmissionStatus status, Pageable pageable);
    
    // Find pending submissions (for teacher)
    Page<EssaySubmission> findByStatusOrderBySubmittedAtAsc(SubmissionStatus status, Pageable pageable);
    
    // Count submissions by status for a question
    long countByEssayQuestionIdAndStatus(Long questionId, SubmissionStatus status);
}
