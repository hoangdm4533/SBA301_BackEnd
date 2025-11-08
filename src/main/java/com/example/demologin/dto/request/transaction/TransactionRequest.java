package com.example.demologin.dto.request.transaction;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequest {
    private Long userId;
    private Long subscriptionId;
    private Double amount;
    private String paymentMethod;
    private String status;
    private String transactionRef;
    private LocalDateTime createdAt;
}
