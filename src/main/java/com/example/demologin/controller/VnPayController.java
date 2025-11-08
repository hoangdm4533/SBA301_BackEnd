package com.example.demologin.controller;

import com.example.demologin.entity.User;
import com.example.demologin.service.VnPayService;
import com.example.demologin.dto.response.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment/vnpay")
@RequiredArgsConstructor
public class VnPayController {

    private final VnPayService vnPayService;

    // API để tạo URL thanh toán VNPay từ PlanId
    @PostMapping("/create-payment-url")
    public ResponseObject createPaymentUrl(@RequestParam Long planId) throws Exception {
        String paymentUrl = vnPayService.createVnPayUrl(planId);

        return new ResponseObject(200, "Payment URL generated successfully", paymentUrl);
    }

    // API xử lý kết quả thanh toán từ VNPay trả về
    @GetMapping("/vnpay-return")
    public ResponseObject handleVnPayReturn(@RequestParam Map<String, String> params) {
        Map<String, String> result = vnPayService.handleVnPayReturn(params);
        return "00".equals(result.get("RspCode"))
                ? new ResponseObject(200, "Payment successful", result)
                : new ResponseObject(400, "Payment failed", result);
    }

    @PostMapping("/cancel-by-subscription")
    @ApiResponse(description = "Cancel payment by subscriptionId")
    @Operation(summary = "Cancel VNPay by subscriptionId")
    public ResponseEntity<ResponseObject> cancelBySubscription(@RequestParam Long subscriptionId,
                                                               @AuthenticationPrincipal User currentUser) {
        try {
            vnPayService.cancelBySubscriptionId(subscriptionId, currentUser.getUserId());
            return ResponseEntity.ok(
                    new ResponseObject(HttpStatus.OK.value(),
                            "Canceled successfully",
                            Map.of("subscriptionId", subscriptionId, "subscriptionStatus", "CANCELLED"))
            );
        } catch (RuntimeException ex) {
            String msg = ex.getMessage();
            if ("Subscription not found".equals(msg)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject(404, msg, null));
            }
            if (msg != null && msg.startsWith("You are not allowed")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ResponseObject(403, msg, null));
            }
            if (msg != null && msg.startsWith("Cannot cancel")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ResponseObject(409, msg, null));
            }
            return ResponseEntity.badRequest().body(new ResponseObject(400, msg, null));
        }
    }
}
