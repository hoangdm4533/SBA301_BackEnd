package com.example.demologin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demologin.entity.EssayAttachment;

@Repository
public interface EssayAttachmentRepository extends JpaRepository<EssayAttachment, Long> {
    List<EssayAttachment> findByEssayQuestionId(Long essayQuestionId);
    
    void deleteByEssayQuestionId(Long essayQuestionId);
}
