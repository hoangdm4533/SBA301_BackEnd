package com.example.demologin.service;

import com.example.demologin.dto.request.payment.PaymentRequest;
import com.example.demologin.dto.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse createCheckoutSession(Long userId, PaymentRequest request) throws Exception;
    void handlePaymentSuccess(String sessionId) throws Exception;
    void handlePaymentFailure(String transactionRef, String reason);
}
