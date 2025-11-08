package com.example.demologin.service;

import com.example.demologin.dto.response.DashboardCardResponse;
import com.example.demologin.dto.response.TransactionResponse;

import java.time.LocalDate;
import java.util.List;

public interface DashboardService {
    Long countUsers();
    Long countPlans();
    Long countTransactions();
    DashboardCardResponse dashboardCard();
    Double getRevenueByToday();
    Double getRevenueByThisMonth();
    Double getRevenueByThisYear();
    Double getRevenueByDate(LocalDate date);
    Double getRevenueByMonth(int year, int month);
    Double getRevenueByYear(int year);

}
