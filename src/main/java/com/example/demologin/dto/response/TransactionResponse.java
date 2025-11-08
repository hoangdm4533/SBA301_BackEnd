package com.example.demologin.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
    private Long id;
    private Long userId;
    private Long subscriptionId;
    private Double amount;
    private String paymentMethod;
    private String status;
    private String transactionRef;
    private LocalDateTime createdAt;
}