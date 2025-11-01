package com.example.demologin.repository;

import com.example.demologin.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    List<Chapter> findByGrade_IdOrderByOrderNoAsc(Long gradeId);
}
