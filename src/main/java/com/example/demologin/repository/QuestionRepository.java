package com.example.demologin.repository;

import com.example.demologin.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByDifficulty(String difficulty);
    List<Question> findByDifficultyAndType(String difficulty, String type);
}

