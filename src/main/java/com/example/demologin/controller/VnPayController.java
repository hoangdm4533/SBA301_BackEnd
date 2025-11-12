package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.entity.User;
import com.example.demologin.service.VnPayService;
import com.example.demologin.dto.response.ResponseObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payment/vnpay")
@RequiredArgsConstructor
@Tag(name = "VNPay Payment", description = "APIs for VNPay payment integration")
public class VnPayController {

    private final VnPayService vnPayService;

    @Value("${vnpay.frontend.callback.url}")
    private String vnpayFrontendCallbackUrl;


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
    @Operation(summary = "VNPay payment return handler",
            description = "Handle payment result callback from VNPay and redirect to frontend")
    public void handleVnPayReturn(
            @RequestParam Map<String, String> params,
            HttpServletResponse response) throws IOException {

        log.info("Received VNPay callback with params: {}", params);

        try {
            // 1. Process payment (validate signature, update DB)
            Map<String, String> result = vnPayService.handleVnPayReturn(params);

            log.info("VNPay payment processing result: {}", result);

            // 2. Build redirect URL with all VNPay params
            StringBuilder redirectUrl = new StringBuilder(vnpayFrontendCallbackUrl);
            redirectUrl.append("?");

            // 3. Add all params from VNPay to redirect URL
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String encodedKey = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8);
                String encodedValue = URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8);
                redirectUrl.append(encodedKey)
                        .append("=")
                        .append(encodedValue)
                        .append("&");
            }

            // Remove trailing &
            if (redirectUrl.charAt(redirectUrl.length() - 1) == '&') {
                redirectUrl.deleteCharAt(redirectUrl.length() - 1);
            }

            String finalRedirectUrl = redirectUrl.toString();
            log.info("Redirecting to frontend: {}", finalRedirectUrl);

            // 4. REDIRECT to frontend (302 Found)
            response.sendRedirect(finalRedirectUrl);

        } catch (Exception e) {
            log.error("Error processing VNPay callback", e);

            // Redirect to error page even on exception
            String errorUrl = vnpayFrontendCallbackUrl + "?vnp_ResponseCode=99&error=processing_failed";
            response.sendRedirect(errorUrl);
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
