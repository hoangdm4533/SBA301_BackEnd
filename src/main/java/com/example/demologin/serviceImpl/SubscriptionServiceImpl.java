package com.example.demologin.serviceImpl;

import com.example.demologin.dto.request.subcription.SubscriptionRequest;
import com.example.demologin.dto.response.PageResponse;
import com.example.demologin.dto.response.SubscriptionResponse;
import com.example.demologin.entity.Plan;
import com.example.demologin.entity.Subscription;
import com.example.demologin.entity.Transaction;
import com.example.demologin.entity.User;
import com.example.demologin.repository.PlanRepository;
import com.example.demologin.repository.SubscriptionRepository;
import com.example.demologin.repository.UserRepository;
import com.example.demologin.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;

    @Override
    public SubscriptionResponse createSubscription(SubscriptionRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Plan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        Subscription subscription = Subscription.builder()
                .user(user)
                .plan(plan)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(request.getStatus())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Subscription saved = subscriptionRepository.save(subscription);
        return mapToResponse(saved);
    }

    @Override
    public SubscriptionResponse updateSubscription(Long id, SubscriptionRequest request) {
        Subscription sub = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        if (request.getPlanId() != null) {
            Plan plan = planRepository.findById(request.getPlanId())
                    .orElseThrow(() -> new RuntimeException("Plan not found"));
            sub.setPlan(plan);
        }

        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            sub.setUser(user);
        }

        sub.setStartDate(request.getStartDate());
        sub.setEndDate(request.getEndDate());
        sub.setStatus(request.getStatus());
        sub.setUpdatedAt(LocalDateTime.now());

        Subscription updated = subscriptionRepository.save(sub);
        return mapToResponse(updated);
    }

    @Override
    public SubscriptionResponse getSubscriptionById(Long id) {
        Subscription sub = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));
        return mapToResponse(sub);
    }

    @Override
    public List<SubscriptionResponse> getAllSubscriptions() {
        return subscriptionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<SubscriptionResponse> getAllSubscriptionsPaged(Long userId, Long planId, String status, Pageable pageable) {
        Page<Subscription> page = subscriptionRepository.search(userId, planId, status, pageable);
        Page<SubscriptionResponse> mappedPage = page.map(this::mapToResponse);
        return new PageResponse<>(mappedPage);
    }

    @Override
    public boolean deleteSubscription(Long id) {
        if (!subscriptionRepository.existsById(id))
            throw new RuntimeException("Subscription not found");
        subscriptionRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean hasPremium(Long userId) {
        return subscriptionRepository.existsByUserUserIdAndStatusAndEndDateAfter(
            userId, "ACTIVE", LocalDateTime.now()
        );
    }

    private SubscriptionResponse mapToResponse(Subscription sub) {
        return SubscriptionResponse.builder()
                .id(sub.getId())
                .userId(sub.getUser().getUserId())
                .planId(sub.getPlan().getId())
                .status(sub.getStatus())
                .startDate(sub.getStartDate())
                .endDate(sub.getEndDate())
                .createdAt(sub.getCreatedAt())
                .updatedAt(sub.getUpdatedAt())
                .transactionIds(sub.getTransactions()
                        .stream()
                        .map(Transaction::getId)
                        .collect(Collectors.toList()))
                .build();
    }
}

