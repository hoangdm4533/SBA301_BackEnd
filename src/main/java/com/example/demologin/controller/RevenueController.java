package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.service.DashboardService;
import com.example.demologin.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard/revenue")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN')")
public class RevenueController {

    private final DashboardService dashboardService;


    // ================================
    // Doanh thu theo ngày, tháng, năm hiện tại
    // ================================
    @ApiResponse
    @Operation(summary = "Doanh thu hôm nay", description = "Lấy tổng doanh thu của ngày hiện tại")
    @GetMapping("/today")
    public Map<String, Double> getTodayRevenue() {
        return Map.of("revenue", dashboardService.getRevenueByToday());
    }

    @ApiResponse
    @Operation(summary = "Doanh thu tháng hiện tại", description = "Lấy tổng doanh thu của tháng hiện tại")
    @GetMapping("/month")
    public Map<String, Double> getThisMonthRevenue() {
        return Map.of("revenue", dashboardService.getRevenueByThisMonth());
    }

    @ApiResponse
    @Operation(summary = "Doanh thu năm hiện tại", description = "Lấy tổng doanh thu của năm hiện tại")
    @GetMapping("/year")
    public Map<String, Double> getThisYearRevenue() {
        return Map.of("revenue", dashboardService.getRevenueByThisYear());
    }

    // ================================
    // Doanh thu theo ngày, tháng, năm chỉ định
    // ================================
    @ApiResponse
    @Operation(summary = "Doanh thu theo ngày chỉ định", description = "Lấy tổng doanh thu của ngày chỉ định")
    @GetMapping("/by-date")
    public Map<String, Double> getRevenueByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return Map.of("revenue", dashboardService.getRevenueByDate(date));
    }

    @ApiResponse
    @Operation(summary = "Doanh thu theo tháng chỉ định", description = "Lấy tổng doanh thu của tháng chỉ định")
    @GetMapping("/by-month")
    public Map<String, Double> getRevenueByMonth(
            @RequestParam("year") int year,
            @RequestParam("month") int month
    ) {
        return Map.of("revenue", dashboardService.getRevenueByMonth(year, month));
    }

    @ApiResponse
    @Operation(summary = "Doanh thu theo năm chỉ định", description = "Lấy tổng doanh thu của năm chỉ định")
    @GetMapping("/by-year")
    public Map<String, Double> getRevenueByYear(
            @RequestParam("year") int year
    ) {
        return Map.of("revenue", dashboardService.getRevenueByYear(year));
    }
}