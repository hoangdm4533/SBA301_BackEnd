package com.example.demologin.service.impl;

import com.example.demologin.dto.response.DashboardCardResponse;
import com.example.demologin.entity.Transaction;
import com.example.demologin.enums.TransactionStatus;
import com.example.demologin.repository.PlanRepository;
import com.example.demologin.repository.TransactionRepository;
import com.example.demologin.repository.UserRepository;
import com.example.demologin.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public Long countUsers() {
        return userRepository.count();
    }

    @Override
    public Long countPlans() {
        return planRepository.count();
    }

    @Override
    public Long countTransactions() {
        return transactionRepository.count();
    }

    @Override
    public DashboardCardResponse dashboardCard() {
        return DashboardCardResponse.builder()
                .users(this.countUsers())
                .plans(this.countPlans())
                .transactions(this.countTransactions())
                .build();
    }

    @Override
    public Double getRevenueByToday() {
        LocalDate today = LocalDate.now();
        return calculateRevenue(today.atStartOfDay(), today.atTime(LocalTime.MAX));
    }

    @Override
    public Double getRevenueByThisMonth() {
        LocalDate today = LocalDate.now();
        LocalDate firstDay = today.withDayOfMonth(1);
        LocalDate lastDay = today.withDayOfMonth(today.lengthOfMonth());
        return calculateRevenue(firstDay.atStartOfDay(), lastDay.atTime(LocalTime.MAX));
    }

    @Override
    public Double getRevenueByThisYear() {
        LocalDate today = LocalDate.now();
        LocalDate firstDay = today.withDayOfYear(1);
        LocalDate lastDay = today.withDayOfYear(today.lengthOfYear());
        return calculateRevenue(firstDay.atStartOfDay(), lastDay.atTime(LocalTime.MAX));
    }

    @Override
    public Double getRevenueByDate(LocalDate date) {
        return calculateRevenue(date.atStartOfDay(), date.atTime(LocalTime.MAX));
    }

    @Override
    public Double getRevenueByMonth(int year, int month) {
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());
        return calculateRevenue(firstDay.atStartOfDay(), lastDay.atTime(LocalTime.MAX));
    }

    @Override
    public Double getRevenueByYear(int year) {
        LocalDate firstDay = LocalDate.of(year, 1, 1);
        LocalDate lastDay = LocalDate.of(year, 12, 31);
        return calculateRevenue(firstDay.atStartOfDay(), lastDay.atTime(LocalTime.MAX));
    }

    // ================================
    // Hàm helper tính tổng doanh thu
    // ================================
    private Double calculateRevenue(LocalDateTime start, LocalDateTime end) {
        List<Transaction> transactions = transactionRepository.findByStatusAndCreatedAtBetween(
                TransactionStatus.SUCCESS, start, end
        );
        return transactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

}
