package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.dto.request.payment.PaymentRequest;
import com.example.demologin.dto.response.PaymentResponse;
import com.example.demologin.entity.User;
import com.example.demologin.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;

    @Value("${stripe.webhook.secret.test}")
    private String webhookSecretTest;

    @Value("${stripe.webhook.secret.live}")
    private String webhookSecretLive;

    @PostMapping("/create-session")
    public PaymentResponse createSession(@RequestBody PaymentRequest request,
                                         Authentication authentication) throws Exception {
        User user = (User) authentication.getPrincipal();
        return paymentService.createCheckoutSession(user.getUserId(), request);
    }

    @PostMapping("/webhook")

    public void stripeWebhook(@RequestBody String payload,
                              @RequestHeader("Stripe-Signature") String sigHeader) throws Exception {
        // chọn secret dựa trên payload (contains livemode) hoặc theo config môi trường:
        boolean isLive = payload.contains("\"livemode\":true");
        String endpointSecret = isLive ? webhookSecretLive : webhookSecretTest;

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            // chữ ký không hợp lệ => trả 400 và dừng xử lý
            System.err.println("⚠️ Stripe webhook signature verification failed: " + e.getMessage());
            throw e;
        }

        String eventType = event.getType();
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();

        switch (eventType) {
            case "checkout.session.completed" -> {
                if (dataObjectDeserializer.getObject().isPresent()) {
                    Session session = (Session) dataObjectDeserializer.getObject().get();
                    // gọi service xử lý: chú ý PaymentService.handlePaymentSuccess nên tự quản lý Stripe.apiKey nếu cần
                    paymentService.handlePaymentSuccess(session.getId());
                }
            }

            case "checkout.session.expired", "checkout.session.async_payment_failed" -> {
                if (dataObjectDeserializer.getObject().isPresent()) {
                    Session session = (Session) dataObjectDeserializer.getObject().get();
                    String transactionRef = session.getMetadata() != null ? session.getMetadata().get("transactionRef") : null;
                    paymentService.handlePaymentFailure(transactionRef, eventType);
                }
            }

            case "payment_intent.payment_failed" -> {
                if (dataObjectDeserializer.getObject().isPresent()) {
                    // payment intent object — bạn có thể parse metadata để tìm transactionRef
                    com.stripe.model.PaymentIntent pi = (com.stripe.model.PaymentIntent) dataObjectDeserializer.getObject().get();
                    String transactionRef = pi.getMetadata() != null ? pi.getMetadata().get("transactionRef") : null;
                    paymentService.handlePaymentFailure(transactionRef, "payment_failed");
                }
            }

            default -> {
                System.out.println("ℹ️ Unhandled Stripe event: " + eventType);
            }
        }
    }


    @PostMapping("/verify-session")
    public void verifySession(@RequestParam String sessionId) throws Exception {

        // Lấy session từ Stripe
        Session session = Session.retrieve(sessionId);
        log.info("Session retrieved: {}", session.toJson());
        String transactionRef = session.getMetadata().get("transactionRef");
        log.info("Session payment status {}", session.getPaymentStatus());
        String status;

        // Kiểm tra payment status từ Stripe
        if ("paid".equals(session.getPaymentStatus())) {
            // Thanh toán thành công → gọi service xử lý
            paymentService.handlePaymentSuccess(sessionId);
            status = "SUCCESS";
        } else {
            // Thanh toán thất bại → gọi service xử lý failure
            paymentService.handlePaymentFailure(transactionRef, "payment_failed");
            status = "FAILED";
        }
    }

}
