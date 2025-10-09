package com.example.demologin.repository;

import com.example.demologin.entity.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    
    // Tìm kiếm theo status
    Page<Exam> findByStatus(String status, Pageable pageable);
    
    // Tìm kiếm theo title có chứa keyword
    @Query("SELECT e FROM Exam e WHERE e.title LIKE %:keyword% OR e.description LIKE %:keyword%")
    Page<Exam> findByTitleContainingOrDescriptionContaining(@Param("keyword") String keyword, Pageable pageable);
    
    // Kiểm tra title trùng lặp
    boolean existsByTitle(String title);
    
    // Lấy exam được publish
    @Query("SELECT e FROM Exam e WHERE e.status = 'PUBLISHED'")
    Page<Exam> findPublishedExams(Pageable pageable);
    
    // Lấy exam theo status
    List<Exam> findByStatus(String status);
    
    // Đếm exam theo level và status (để thay thế cho ExamTemplate)
    long countByStatus(String status);
}
