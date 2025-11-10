package com.example.demologin.controller;

import com.example.demologin.annotation.AuthenticatedEndpoint;
import com.example.demologin.dto.request.subcription.SubscriptionRequest;
import com.example.demologin.dto.response.PageResponse;
import com.example.demologin.dto.response.SubscriptionResponse;
import com.example.demologin.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscription API", description = "CRUD operations for subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "Create new subscription")
    @PostMapping
    public ResponseEntity<SubscriptionResponse> create(@RequestBody SubscriptionRequest request) {
        return ResponseEntity.ok(subscriptionService.createSubscription(request));
    }

    @Operation(summary = "Update existing subscription by ID")
    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionResponse> update(@PathVariable Long id, @RequestBody SubscriptionRequest request) {
        return ResponseEntity.ok(subscriptionService.updateSubscription(id, request));
    }

    @Operation(summary = "Get subscription by ID")
    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionById(id));
    }

    @Operation(summary = "Get all subscriptions")
    @GetMapping
    public ResponseEntity<List<SubscriptionResponse>> getAll() {
        return ResponseEntity.ok(subscriptionService.getAllSubscriptions());
    }

    @Operation(summary = "Get subscriptions with pagination and optional filters")
    @GetMapping("paged")
    public PageResponse<SubscriptionResponse> getPaged(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long planId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return subscriptionService.getAllSubscriptionsPaged(userId, planId, status, pageable);
    }

    @Operation(summary = "Delete subscription by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get my subscriptions", description = "For students to view their own subscription history")
    @GetMapping("/my-subscriptions")
    @AuthenticatedEndpoint
    public ResponseEntity<List<SubscriptionResponse>> getMySubscriptions() {
        return ResponseEntity.ok(subscriptionService.getMySubscriptions());
    }
}
