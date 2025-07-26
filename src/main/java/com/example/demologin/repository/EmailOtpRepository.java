package com.example.demologin.repository;

import com.example.demologin.entity.EmailOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {
    Optional<EmailOtp> findTopByEmailAndTypeOrderByCreatedAtDesc(String email, String type);
    List<EmailOtp> findByEmailAndTypeAndVerifiedFalse(String email, String type);
    void deleteByEmailAndType(String email, String type);
} 