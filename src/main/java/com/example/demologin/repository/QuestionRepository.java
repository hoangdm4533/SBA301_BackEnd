package com.example.demologin.repository;

import com.example.demologin.entity.Question;
import com.example.demologin.entity.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByType(QuestionType type);
}

