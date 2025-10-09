package com.example.demologin.dto.request.subcription;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionRequest {
    private Long userId;
    private Long planId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
}
