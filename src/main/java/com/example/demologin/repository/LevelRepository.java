package com.example.demologin.repository;

import com.example.demologin.entity.Level;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LevelRepository extends JpaRepository<Level, Long> {
    Optional<Level> findByDifficulty(String difficulty);
    boolean existsByDifficultyIgnoreCase(String difficulty);

    @Query(
            value = """
            SELECT *
            FROM levels l
            WHERE (:keyword IS NULL
                   OR LOWER(l.difficulty) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR CAST(l.score AS CHAR) LIKE CONCAT('%', :keyword, '%'))
            """,
            countQuery = """
            SELECT COUNT(*)
            FROM levels l
            WHERE (:keyword IS NULL
                   OR LOWER(l.difficulty) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR CAST(l.score AS CHAR) LIKE CONCAT('%', :keyword, '%'))
            """,
            nativeQuery = true
    )
    Page<Level> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
