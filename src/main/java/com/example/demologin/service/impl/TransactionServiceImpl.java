package com.example.demologin.service.impl;

import com.example.demologin.dto.request.transaction.TransactionRequest;
import com.example.demologin.dto.response.PageResponse;
import com.example.demologin.dto.response.TransactionResponse;
import com.example.demologin.entity.Subscription;
import com.example.demologin.entity.Transaction;
import com.example.demologin.entity.User;
import com.example.demologin.enums.TransactionStatus;
import com.example.demologin.repository.SubscriptionRepository;
import com.example.demologin.repository.TransactionRepository;
import com.example.demologin.repository.UserRepository;
import com.example.demologin.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    public TransactionResponse create(TransactionRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Subscription subscription = subscriptionRepository.findById(request.getSubscriptionId())
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        Transaction transaction = Transaction.builder()
                .user(user)
                .subscription(subscription)
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .status(TransactionStatus.valueOf(request.getStatus()))
                .transactionRef(request.getTransactionRef())
                .createdAt(request.getCreatedAt() != null ? request.getCreatedAt() : java.time.LocalDateTime.now())
                .build();

        Transaction saved = transactionRepository.save(transaction);
        return mapToResponse(saved);
    }

    @Override
    public TransactionResponse update(Long id, TransactionRequest request) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (request.getUserId() != null) {
            transaction.setUser(userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found")));
        }
        if (request.getSubscriptionId() != null) {
            transaction.setSubscription(subscriptionRepository.findById(request.getSubscriptionId())
                    .orElseThrow(() -> new RuntimeException("Subscription not found")));
        }

        transaction.setAmount(request.getAmount());
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setStatus(TransactionStatus.valueOf(request.getStatus()));
        transaction.setTransactionRef(request.getTransactionRef());
        transaction.setCreatedAt(request.getCreatedAt());

        Transaction updated = transactionRepository.save(transaction);
        return mapToResponse(updated);
    }

    @Override
    public void delete(Long id) {
        transactionRepository.deleteById(id);
    }

    @Override
    public TransactionResponse getById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        return mapToResponse(transaction);
    }

    @Override
    public List<TransactionResponse> getAll() {
        return transactionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<TransactionResponse> getAllPaged(Pageable pageable) {
        Page<Transaction> page = transactionRepository.findAll(pageable);
        Page<TransactionResponse> mapped = page.map(this::mapToResponse);
        return new PageResponse<>(mapped);
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .userId(transaction.getUser() != null ? transaction.getUser().getUserId() : null)
                .subscriptionId(transaction.getSubscription() != null ? transaction.getSubscription().getId() : null)
                .amount(transaction.getAmount())
                .paymentMethod(transaction.getPaymentMethod())
                .status(String.valueOf(transaction.getStatus()))
                .transactionRef(transaction.getTransactionRef())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
