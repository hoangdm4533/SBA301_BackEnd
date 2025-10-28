package com.example.demologin.repository;

import com.example.demologin.entity.EssayQuestion;
import com.example.demologin.enums.QuestionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EssayQuestionRepository extends JpaRepository<EssayQuestion, Long> {
    Page<EssayQuestion> findByStatus(QuestionStatus status, Pageable pageable);
    
    List<EssayQuestion> findByStatusOrderByCreatedAtDesc(QuestionStatus status);
    
    Page<EssayQuestion> findByCreatedByUserIdOrderByCreatedAtDesc(Long teacherId, Pageable pageable);
}
