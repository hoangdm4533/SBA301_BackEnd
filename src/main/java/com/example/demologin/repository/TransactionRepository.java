package com.example.demologin.repository;

import com.example.demologin.entity.Transaction;
import com.example.demologin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
