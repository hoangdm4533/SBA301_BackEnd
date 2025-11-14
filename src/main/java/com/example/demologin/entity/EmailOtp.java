package com.example.demologin.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "email_otps")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailOtp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = 255)
    String email;

    @Column(nullable = false, length = 10)
    String otp;

    @Column(nullable = false, length = 30)
    String type; // VERIFY_EMAIL, FORGOT_PASSWORD, ...

    @Column(nullable = false)
    LocalDateTime expiredAt;

    @Column(nullable = false)
    boolean verified = false;

    @Column(nullable = false)
    LocalDateTime createdAt = LocalDateTime.now();
} 
