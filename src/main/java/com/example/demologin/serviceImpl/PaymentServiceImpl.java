package com.example.demologin.serviceImpl;

import com.example.demologin.dto.request.payment.PaymentRequest;
import com.example.demologin.dto.response.PaymentResponse;
import com.example.demologin.entity.Plan;
import com.example.demologin.entity.Subscription;
import com.example.demologin.entity.Transaction;
import com.example.demologin.entity.User;
import com.example.demologin.enums.TransactionStatus;
import com.example.demologin.repository.PlanRepository;
import com.example.demologin.repository.SubscriptionRepository;
import com.example.demologin.repository.TransactionRepository;
import com.example.demologin.repository.UserRepository;
import com.example.demologin.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final SubscriptionRepository subscriptionRepository;
    @Value("${stripe.api.key.test}")
    private String stripeSecretKeyTest;

    @Value("${stripe.api.key.live}")
    private String stripeSecretKeyLive;
    @Override
    @Transactional
    public PaymentResponse createCheckoutSession(Long userId, PaymentRequest request) throws Exception {

    // 1️⃣ Lấy thông tin người dùng và plan
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
    Plan plan = planRepository.findById(request.getPlanId())
            .orElseThrow(() -> new RuntimeException("Plan not found"));


    // 2️⃣ Cấu hình Stripe key theo chế độ
        String apiKey = "sandbox".equalsIgnoreCase(request.getPaymentMode())
                ? stripeSecretKeyTest
                : stripeSecretKeyLive;
        Stripe.apiKey = apiKey;

    // 3️⃣ Tạo Transaction pending
    String transactionRef = UUID.randomUUID().toString();
    Transaction tx = Transaction.builder()
            .user(user)
            .subscription(null)
            .amount(plan.getPrice())
            .paymentMethod("STRIPE")
            .transactionRef(transactionRef)
            .status(TransactionStatus.PENDING)
            .createdAt(LocalDateTime.now())
            .build();
        transactionRepository.save(tx);

    // 4️⃣ Tạo Stripe Checkout session
    SessionCreateParams params = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.PAYMENT)
            .setSuccessUrl(request.getSuccessUrl() + "?session_id={CHECKOUT_SESSION_ID}")
            .setCancelUrl(request.getCancelUrl())
            .addLineItem(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("vnd")
                                            .setUnitAmount((long) (plan.getPrice() * 100)) // cents
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                            .setName(plan.getName())
                                                            .build())
                                            .build()
                            ).build()
            )
            .putMetadata("transactionRef", transactionRef)
            .putMetadata("userId", userId.toString())
            .putMetadata("planId", plan.getId().toString())
            .build();

    Session session = Session.create(params);

    // 5️⃣ Trả về URL cho client
        return new PaymentResponse(session.getUrl(), transactionRef);
}

@Transactional
public void handlePaymentSuccess(String sessionId) throws Exception {
    Session session = Session.retrieve(sessionId);
    String transactionRef = session.getMetadata().get("transactionRef");
    Long userId = Long.parseLong(session.getMetadata().get("userId"));
    Long planId = Long.parseLong(session.getMetadata().get("planId"));

    Transaction tx = transactionRepository.findByTransactionRef(transactionRef)
            .orElseThrow(() -> new RuntimeException("Transaction not found"));

    // 1️⃣ Tạo Subscription mới
    User user = userRepository.findById(userId).orElseThrow();
    Plan plan = planRepository.findById(planId).orElseThrow();

    Subscription sub = Subscription.builder()
            .user(user)
            .plan(plan)
            .status("ACTIVE")
            .startDate(LocalDateTime.now())
            .endDate(LocalDateTime.now().plusDays(plan.getDurationDays()))
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    subscriptionRepository.save(sub);

    // 2️⃣ Cập nhật Transaction
    tx.setStatus(TransactionStatus.SUCCESS);
    tx.setSubscription(sub);
    transactionRepository.save(tx);
}

@Transactional
public void handlePaymentFailure(String transactionRef, String reason) {
    transactionRepository.findByTransactionRef(transactionRef).ifPresent(tx -> {
        tx.setStatus(TransactionStatus.FAILED);
        transactionRepository.save(tx);
    });
}


}
