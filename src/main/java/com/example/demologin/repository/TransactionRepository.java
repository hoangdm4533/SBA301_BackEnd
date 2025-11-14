package com.example.demologin.repository;

import com.example.demologin.entity.Transaction;
import com.example.demologin.entity.User;
import com.example.demologin.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTransactionRef(String transactionRef);

    @Query("SELECT t FROM Transaction t WHERE t.status = :status AND t.createdAt BETWEEN :start AND :end")
    List<Transaction> findByStatusAndCreatedAtBetween(
            @Param("status") TransactionStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    Optional<Transaction> findTopBySubscriptionIdOrderByCreatedAtDesc(Long subscriptionId);
}
