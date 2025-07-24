package com.example.demologin.entity;

import com.example.demologin.enums.AdminActionType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AdminActionLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long adminId;
    private String targetType;
    private String targetId;

    @Enumerated(EnumType.STRING)
    private AdminActionType actionType;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(columnDefinition = "TEXT")
    private String changeSummary;

    private LocalDateTime actionTime = LocalDateTime.now();
} 