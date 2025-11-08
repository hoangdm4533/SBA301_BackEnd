package com.example.demologin.dto.request.payment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentRequest {
    private Long planId;
    private String paymentMode; // "live" hoặc "sandbox"
    private String successUrl;
    private String cancelUrl;
}