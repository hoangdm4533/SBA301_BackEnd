package com.example.demologin.repository;

import com.example.demologin.entity.UserActionLog;
import com.example.demologin.enums.UserActionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserActionLogRepository extends JpaRepository<UserActionLog, Long> {
    
    Page<UserActionLog> findByUserIdOrderByActionTimeDesc(Long userId, Pageable pageable);
    
    Page<UserActionLog> findByUsernameContainingIgnoreCaseOrderByActionTimeDesc(String username, Pageable pageable);
    
    Page<UserActionLog> findByActionTypeOrderByActionTimeDesc(UserActionType actionType, Pageable pageable);
    
    Page<UserActionLog> findByTargetTypeOrderByActionTimeDesc(String targetType, Pageable pageable);
    
    List<UserActionLog> findByActionTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    @Query("SELECT ual FROM UserActionLog ual WHERE " +
           "(:userId IS NULL OR ual.userId = :userId) AND " +
           "(:username IS NULL OR ual.username LIKE %:username%) AND " +
           "(:actionType IS NULL OR ual.actionType = :actionType) AND " +
           "(:targetType IS NULL OR ual.targetType = :targetType) AND " +
           "(:startTime IS NULL OR ual.actionTime >= :startTime) AND " +
           "(:endTime IS NULL OR ual.actionTime <= :endTime) " +
           "ORDER BY ual.actionTime DESC")
    Page<UserActionLog> findWithFilters(
        @Param("userId") Long userId,
        @Param("username") String username,
        @Param("actionType") UserActionType actionType,
        @Param("targetType") String targetType,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime,
        Pageable pageable
    );
}
