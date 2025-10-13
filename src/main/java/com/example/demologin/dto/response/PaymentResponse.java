package com.example.demologin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentResponse {
    private String checkoutUrl;
    private String transactionRef;
}