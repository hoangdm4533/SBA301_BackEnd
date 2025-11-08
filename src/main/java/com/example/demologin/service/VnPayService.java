package com.example.demologin.service;

import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.entity.Subscription;

import java.util.Map;

public interface VnPayService {

    // Tạo URL thanh toán VNPay từ PlanId
    String createVnPayUrl(Long planId) throws Exception;

    // Xử lý kết quả từ VNPay trả về
    Map<String, String> handleVnPayReturn(Map<String, String> params);

    // Xử lý thanh toán thành công
    void processSuccessfulPayment(Subscription subscription, String transactionId);

    // Xử lý thanh toán thất bại
    void processFailedPayment(Subscription subscription);
    void cancelBySubscriptionId(Long subscriptionId, Long currentUserId);

}
