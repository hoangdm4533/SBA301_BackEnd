package com.example.demologin.repository;

import com.example.demologin.entity.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionTypeRepository extends JpaRepository<QuestionType, Long> {
    Optional<QuestionType> findByDescriptionIgnoreCase(String description);
}
