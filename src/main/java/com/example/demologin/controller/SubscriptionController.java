package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.AuthenticatedEndpoint;
import com.example.demologin.annotation.PageResponse;
import com.example.demologin.dto.request.subcription.SubscriptionRequest;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.dto.response.SubscriptionResponse;
import com.example.demologin.entity.User;
import com.example.demologin.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscription API", description = "CRUD operations for subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    @ApiResponse(message = "Subscription created successfully")
    @Operation(summary = "Create new subscription", description = "Create a new subscription for a user")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<ResponseObject> create(@RequestBody SubscriptionRequest request) {
        final SubscriptionResponse data = subscriptionService.createSubscription(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseObject(
                HttpStatus.CREATED.value(),
                "Subscription created successfully",
                data
        ));
    }

    @PutMapping("/{id}")
    @ApiResponse(message = "Subscription updated successfully")
    @Operation(summary = "Update existing subscription by ID", description = "Update subscription details")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> update(@PathVariable Long id, @RequestBody SubscriptionRequest request) {
        final SubscriptionResponse data = subscriptionService.updateSubscription(id, request);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Subscription updated successfully",
                data
        ));
    }

    @GetMapping("/{id}")
    @ApiResponse(message = "Subscription retrieved successfully")
    @Operation(summary = "Get subscription by ID", description = "Retrieve subscription details by ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<ResponseObject> getById(@PathVariable Long id) {
        final SubscriptionResponse data = subscriptionService.getSubscriptionById(id);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Subscription retrieved successfully",
                data
        ));
    }

    @GetMapping
    @ApiResponse(message = "Subscriptions retrieved successfully")
    @Operation(summary = "Get all subscriptions", description = "Retrieve all subscriptions (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getAll() {
        final List<SubscriptionResponse> data = subscriptionService.getAllSubscriptions();
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Subscriptions retrieved successfully",
                data
        ));
    }

    @GetMapping("/paged")
    @ApiResponse(message = "Subscriptions retrieved successfully")
    @PageResponse
    @Operation(summary = "Get subscriptions with pagination and optional filters", description = "Get paginated subscriptions with filters")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> getPaged(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long planId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        final com.example.demologin.dto.response.PageResponse<SubscriptionResponse> data =
                subscriptionService.getAllSubscriptionsPaged(userId, planId, status, pageable);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Subscriptions retrieved successfully",
                data
        ));
    }

    @DeleteMapping("/{id}")
    @ApiResponse(message = "Subscription deleted successfully")
    @Operation(summary = "Delete subscription by ID", description = "Delete a subscription (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> delete(@PathVariable Long id) {
        subscriptionService.deleteSubscription(id);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Subscription deleted successfully",
                id
        ));
    }

    @GetMapping("/check-active")
    @ApiResponse(message = "Active subscription retrieved successfully")
    @Operation(summary = "Check active subscription status", 
               description = "Get current active subscription with remaining time for the logged-in user")
    @AuthenticatedEndpoint
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ResponseObject> checkActiveSubscription(
            @AuthenticationPrincipal User currentUser) {
        try {
            final SubscriptionResponse data = subscriptionService.getActiveSubscription(currentUser.getUserId());
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.value(),
                    "Active subscription found",
                    data
            ));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject(
                    HttpStatus.NOT_FOUND.value(),
                    "No active subscription found",
                    null
            ));
        }
    }
}
