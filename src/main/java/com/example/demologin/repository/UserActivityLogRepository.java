package com.example.demologin.repository;

import com.example.demologin.entity.UserActivityLog;
import com.example.demologin.enums.ActivityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {
    
    // Find by user ID
    Page<UserActivityLog> findByUserIdOrderByTimestampDesc(Long userId, Pageable pageable);
    
    // Find by activity type
    Page<UserActivityLog> findByActivityTypeOrderByTimestampDesc(ActivityType activityType, Pageable pageable);

    
    // Find by date range
    @Query("SELECT u FROM UserActivityLog u WHERE u.timestamp BETWEEN :startTime AND :endTime ORDER BY u.timestamp DESC")
    Page<UserActivityLog> findByTimestampBetween(@Param("startTime") LocalDateTime startTime, 
                                                @Param("endTime") LocalDateTime endTime, 
                                                Pageable pageable);
    
    // Find existing log to update instead of creating duplicate
    UserActivityLog findTopByUserIdAndActivityTypeAndIpAddressAndUserAgentOrderByTimestampDesc(
        Long userId, ActivityType activityType, String ipAddress, String userAgent);
    
    // Find login history for a specific user
    Page<UserActivityLog> findByUserIdAndActivityTypeOrderByTimestampDesc(
        Long userId, ActivityType activityType, Pageable pageable);

    void deleteByUserId(Long userId);
}

