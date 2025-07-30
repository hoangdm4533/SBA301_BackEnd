package com.example.demologin.entity;

import com.example.demologin.enums.UserActionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_action_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserActionLog {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long userId;
    private String username;
    private String roleName;
    
    @Enumerated(EnumType.STRING)
    private UserActionType actionType;
    
    private String targetType;
    private String targetId;
    private String targetName;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String reason;
    
    @Column(columnDefinition = "TEXT")
    private String changeSummary;
    
    private String ipAddress;
    private String userAgent;
    
    @Builder.Default
    private LocalDateTime actionTime = LocalDateTime.now();
}
