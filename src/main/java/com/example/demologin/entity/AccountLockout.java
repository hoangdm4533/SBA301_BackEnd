package com.example.demologin.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_lockouts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AccountLockout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(nullable = false)
    private LocalDateTime lockTime;

    @Column(nullable = false)
    private LocalDateTime unlockTime;

    @Column(nullable = false, length = 500)
    private String reason;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}
