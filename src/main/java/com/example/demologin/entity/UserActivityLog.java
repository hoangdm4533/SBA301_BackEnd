package com.example.demologin.entity;

import com.example.demologin.enums.ActivityType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_activity_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ActivityType activityType;

    @Column(nullable = true)
    Long userId;

    @Column(length = 100)
    String fullName;

    @Column(nullable = false)
    LocalDateTime timestamp;


    @Column(nullable = false, length = 20)
    @Builder.Default
    String status = "SUCCESS";

    @Column(length = 500)
    String details;

    @Column(length = 45)
    String ipAddress;

    @Column(length = 1000)
    String userAgent;

    @Column(length = 100)
    String browser;

    @Column(length = 50)
    String browserVersion;

    @Column(length = 100)
    String operatingSystem;

    @Column(length = 100)
    String device;

    @Column(length = 20)
    String deviceType;

    @Column(length = 100)
    String city;

    @Column(length = 100)
    String region;

    @Column(length = 100)
    String country;

    @Column(length = 10)
    String countryCode;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        if (status == null) {
            status = "SUCCESS";
        }
    }
}
