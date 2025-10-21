package com.example.demologin.initializer.components;

import com.example.demologin.entity.Plan;
import com.example.demologin.entity.Subscription;
import com.example.demologin.entity.Transaction;
import com.example.demologin.entity.User;
import com.example.demologin.enums.TransactionStatus;
import com.example.demologin.repository.PlanRepository;
import com.example.demologin.repository.SubscriptionRepository;
import com.example.demologin.repository.TransactionRepository;
import com.example.demologin.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlanDataInitializer {

    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Transactional
    public void initializePlansAndSubscriptions() {
        if (planRepository.count() == 0) {
            seedPlans();
        }
        if (subscriptionRepository.count() == 0) {
            seedSubscriptionForMember();
        }
    }

    private void seedPlans() {
        LocalDateTime now = LocalDateTime.now();
        Plan basic = Plan.builder()
                .name("Basic")
                .description("Basic monthly plan")
                .price(9.99)
                .durationDays(30)
                .createdAt(now)
                .build();
        Plan premium = Plan.builder()
                .name("Premium")
                .description("Premium quarterly plan")
                .price(24.99)
                .durationDays(90)
                .createdAt(now)
                .build();
        Plan annual = Plan.builder()
                .name("Annual")
                .description("Annual plan with best value")
                .price(79.99)
                .durationDays(365)
                .createdAt(now)
                .build();
        planRepository.saveAll(List.of(basic, premium, annual));
        log.info("✅ Seeded {} plans", planRepository.count());
    }

    private void seedSubscriptionForMember() {
        User member = userRepository.findByUsername("member").orElse(null);
        User admin = userRepository.findByUsername("admin").orElse(null);
        if (member == null && admin == null) {
            log.warn("⚠️ No users found to attach subscriptions");
            return;
        }
        List<Plan> plans = planRepository.findAll();
        if (plans.isEmpty()) {
            log.warn("⚠️ No plans found to create subscriptions");
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        // Helper to create sub+txn
        java.util.function.BiConsumer<User, Plan> create = (user, plan) -> {
            if (user == null || plan == null) return;
            Subscription sub = Subscription.builder()
                    .user(user)
                    .plan(plan)
                    .status("ACTIVE")
                    .startDate(now.minusDays(1))
                    .endDate(now.plusDays(plan.getDurationDays() != null ? plan.getDurationDays() : 30))
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            subscriptionRepository.save(sub);

            Transaction txn = Transaction.builder()
                    .amount(plan.getPrice())
                    .paymentMethod("CASH")
                    .status(TransactionStatus.SUCCESS)
                    .transactionRef("TXN-" + UUID.randomUUID())
                    .createdAt(now)
                    .user(user)
                    .subscription(sub)
                    .build();
            transactionRepository.save(txn);
            log.info("✅ Seeded subscription and transaction: user='{}', plan='{}'", user.getUsername(), plan.getName());
        };

        // Assign plans
        Plan p0 = plans.get(0);
        Plan p1 = plans.size() > 1 ? plans.get(1) : p0;
        if (member != null) create.accept(member, p0);
        if (admin != null) create.accept(admin, p1);
    }
}
