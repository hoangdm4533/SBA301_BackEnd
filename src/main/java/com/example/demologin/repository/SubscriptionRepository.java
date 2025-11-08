package com.example.demologin.repository;

import com.example.demologin.entity.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    @Query("""
        SELECT s FROM Subscription s
        WHERE (:userId IS NULL OR s.user.userId = :userId)
          AND (:planId IS NULL OR s.plan.id = :planId)
          AND (:status IS NULL OR s.status = :status)
        """)
    Page<Subscription> search(@Param("userId") Long userId,
                              @Param("planId") Long planId,
                              @Param("status") String status,
                              Pageable pageable);
    
    boolean existsByUserUserIdAndStatusAndEndDateAfter(Long userId, String status, LocalDateTime date);
}
