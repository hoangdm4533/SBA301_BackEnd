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
    boolean existsByTransactionRef(String transactionRef);
    // Tìm transaction theo transactionRef (duy nhất)
    Optional<Transaction> findByTransactionRef(String transactionRef);

    // Lấy danh sách transaction của một user
    List<Transaction> findAllByUser(User user);

    // Hoặc theo userId nếu muốn tránh join object
    List<Transaction> findAllByUser_UserId(Long userId);

    @Query("SELECT t FROM Transaction t WHERE t.status = :status AND t.createdAt BETWEEN :start AND :end")
    List<Transaction> findByStatusAndCreatedAtBetween(
            @Param("status") TransactionStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
