package com.example.demologin.repository;

import com.example.demologin.entity.UserActionLog;
import com.example.demologin.enums.UserActionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserActionLogRepository extends JpaRepository<UserActionLog, Long> {
    
    Page<UserActionLog> findByUserIdOrderByActionTimeDesc(Long userId, Pageable pageable);
    
    Page<UserActionLog> findByActionTypeOrderByActionTimeDesc(UserActionType actionType, Pageable pageable);
    
    Page<UserActionLog> findByTargetTypeOrderByActionTimeDesc(String targetType, Pageable pageable);
    
    List<UserActionLog> findByActionTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
}
