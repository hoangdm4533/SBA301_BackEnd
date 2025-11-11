package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.entity.User;
import com.example.demologin.service.VnPayService;
import com.example.demologin.dto.response.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment/vnpay")
@RequiredArgsConstructor
@Tag(name = "VNPay Payment", description = "APIs for VNPay payment integration")
public class VnPayController {

    private final VnPayService vnPayService;

    // API để tạo URL thanh toán VNPay từ PlanId
    @PostMapping("/create-payment-url")
    @ApiResponse(message = "Payment URL generated successfully")
    @Operation(summary = "Create VNPay payment URL",
               description = "Generate VNPay payment URL for a specific plan")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> createPaymentUrl(@RequestParam Long planId) throws Exception {
        String paymentUrl = vnPayService.createVnPayUrl(planId);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Payment URL generated successfully",
                paymentUrl
        ));
    }

    // API xử lý kết quả thanh toán từ VNPay trả về
    @GetMapping("/vnpay-return")
    @ApiResponse(message = "Payment processed successfully")
    @Operation(summary = "VNPay payment return handler",
               description = "Handle payment result callback from VNPay")
    public ResponseEntity<ResponseObject> handleVnPayReturn(@RequestParam Map<String, String> params) {
        Map<String, String> result = vnPayService.handleVnPayReturn(params);

        if ("00".equals(result.get("RspCode"))) {
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.value(),
                    "Payment successful",
                    result
            ));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject(
                    HttpStatus.BAD_REQUEST.value(),
                    "Payment failed",
                    result
            ));
        }
    }

    @PostMapping("/cancel-by-subscription")
    @ApiResponse(message = "Payment cancelled successfully")
    @Operation(summary = "Cancel VNPay payment by subscription",
               description = "Cancel payment by subscription ID")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<ResponseObject> cancelBySubscription(
            @RequestParam Long subscriptionId,
            @AuthenticationPrincipal User currentUser) {
        try {
            vnPayService.cancelBySubscriptionId(subscriptionId, currentUser.getUserId());
            return ResponseEntity.ok(new ResponseObject(
                    HttpStatus.OK.value(),
                    "Cancelled successfully",
                    Map.of("subscriptionId", subscriptionId, "subscriptionStatus", "CANCELLED")
            ));
        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("Subscription not found".equals(msg)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(HttpStatus.NOT_FOUND.value(), msg, null));
            }
            if (msg != null && msg.startsWith("You are not allowed")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(HttpStatus.FORBIDDEN.value(), msg, null));
            }
            if (msg != null && msg.startsWith("Cannot cancel")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ResponseObject(HttpStatus.CONFLICT.value(), msg, null));
            }
            return ResponseEntity.badRequest()
                    .body(new ResponseObject(HttpStatus.BAD_REQUEST.value(), msg, null));
        }
    }
}
