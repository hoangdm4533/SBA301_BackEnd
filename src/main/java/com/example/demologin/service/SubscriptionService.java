package com.example.demologin.service;


import com.example.demologin.dto.request.subcription.SubscriptionRequest;
import com.example.demologin.dto.response.PageResponse;
import com.example.demologin.dto.response.SubscriptionResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubscriptionService {
    SubscriptionResponse createSubscription(SubscriptionRequest request);
    SubscriptionResponse updateSubscription(Long id, SubscriptionRequest request);
    SubscriptionResponse getSubscriptionById(Long id);
    List<SubscriptionResponse> getAllSubscriptions();
    PageResponse<SubscriptionResponse> getAllSubscriptionsPaged(Long userId, Long planId, String status, Pageable pageable);
    void deleteSubscription(Long id);
    boolean hasPremium(Long userId);
}
