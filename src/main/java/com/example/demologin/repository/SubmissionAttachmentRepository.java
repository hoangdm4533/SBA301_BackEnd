package com.example.demologin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demologin.entity.SubmissionAttachment;

@Repository
public interface SubmissionAttachmentRepository extends JpaRepository<SubmissionAttachment, Long> {
    List<SubmissionAttachment> findBySubmissionId(Long submissionId);
    
    void deleteBySubmissionId(Long submissionId);
}
