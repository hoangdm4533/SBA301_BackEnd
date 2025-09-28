package com.example.demologin.repository;

import com.example.demologin.entity.ExamTemplate;
import com.example.demologin.entity.Level;
import com.example.demologin.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExamTemplateRepository extends JpaRepository<ExamTemplate, Long> {
    
    List<ExamTemplate> findByLevel(Level level);
    
    List<ExamTemplate> findByStatus(String status);
    
    Page<ExamTemplate> findByStatus(String status, Pageable pageable);
    
    List<ExamTemplate> findByCreatedBy(User createdBy);
    
    List<ExamTemplate> findByLevelAndStatus(Level level, String status);
    
    @Query("SELECT et FROM ExamTemplate et WHERE et.level.id = :levelId AND et.status = :status ORDER BY et.createdAt DESC")
    Page<ExamTemplate> findByLevelIdAndStatus(@Param("levelId") Long levelId, @Param("status") String status, Pageable pageable);
    
    @Query("SELECT et FROM ExamTemplate et WHERE et.title LIKE %:keyword% OR et.description LIKE %:keyword%")
    Page<ExamTemplate> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT et FROM ExamTemplate et WHERE et.level.id = :levelId AND (et.title LIKE %:keyword% OR et.description LIKE %:keyword%)")
    Page<ExamTemplate> findByLevelIdAndKeyword(@Param("levelId") Long levelId, @Param("keyword") String keyword, Pageable pageable);
    
    Optional<ExamTemplate> findByIdAndCreatedBy(Long id, User createdBy);
    
    boolean existsByTitleIgnoreCase(String title);
    
    long countByLevelAndStatus(Level level, String status);
    
    @Query("SELECT et FROM ExamTemplate et JOIN FETCH et.level")
    List<ExamTemplate> findAllWithLevel();
}