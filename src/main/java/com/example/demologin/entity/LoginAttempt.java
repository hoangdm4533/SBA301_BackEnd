package com.example.demologin.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "login_attempts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LoginAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(nullable = false, length = 45)
    private String ipAddress;

    @Column(nullable = false)
    private boolean success;

    @Column(nullable = false)
    private LocalDateTime attemptTime;

    @Column(length = 500)
    private String failureReason;
}
