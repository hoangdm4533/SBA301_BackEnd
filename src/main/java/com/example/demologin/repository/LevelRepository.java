package com.example.demologin.repository;

import com.example.demologin.entity.Level;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LevelRepository extends JpaRepository<Level, Long> {
    
    List<Level> findByNameContainingIgnoreCase(String keyword);
    
    List<Level> findByStatus(String status);
    
    List<Level> findByDifficulty(String difficulty);
    
    @Query("SELECT l FROM Level l WHERE l.status = 'ACTIVE' ORDER BY l.minScore ASC")
    List<Level> findAllActiveOrderByMinScore();
    
    @Query("SELECT l FROM Level l WHERE l.name LIKE %:keyword% OR l.description LIKE %:keyword%")
    Page<Level> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    Optional<Level> findByIdAndStatus(Long id, String status);
    
    boolean existsByNameIgnoreCase(String name);
}