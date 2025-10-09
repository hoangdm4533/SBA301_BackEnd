package com.example.demologin.dto.response;


import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@Setter
@AllArgsConstructor
@Builder
public class SubscriptionResponse {
    private Long id;
    private Long userId;
    private Long planId;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Long> transactionIds;
}
