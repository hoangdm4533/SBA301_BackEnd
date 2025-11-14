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
    Optional<EssaySubmission> findByUserUserIdAndEssayQuestionId(Long userId, Long questionId);
    
    Optional<EssaySubmission> findByUserUserIdAndEssayQuestionIdAndStatus(
        Long userId, Long questionId, SubmissionStatus status);
    
    Page<EssaySubmission> findByUserUserIdOrderByStartedAtDesc(Long userId, Pageable pageable);
    
    Page<EssaySubmission> findByEssayQuestionIdOrderBySubmittedAtDesc(Long questionId, Pageable pageable);
    
    Page<EssaySubmission> findByStatusOrderBySubmittedAtDesc(SubmissionStatus status, Pageable pageable);
    
    Page<EssaySubmission> findByStatusOrderBySubmittedAtAsc(SubmissionStatus status, Pageable pageable);
    
    Page<EssaySubmission> findByStatusAndEssayQuestionCreatedByUserIdOrderBySubmittedAtAsc(
        SubmissionStatus status, Long teacherId, Pageable pageable);
    

}
