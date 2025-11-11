package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard/revenue")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN')")
@Tag(name = "Revenue Dashboard", description = "Revenue statistics and analytics endpoints")
public class RevenueController {

    private final DashboardService dashboardService;


    // ================================
    // Doanh thu theo ngày, tháng, năm hiện tại
    // ================================
    @ApiResponse(message = "Today's revenue retrieved successfully")
    @Operation(summary = "Doanh thu hôm nay", description = "Lấy tổng doanh thu của ngày hiện tại")
    @GetMapping("/today")
    public ResponseEntity<ResponseObject> getTodayRevenue() {
        double revenue = dashboardService.getRevenueByToday();
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Today's revenue retrieved successfully",
                Map.of("revenue", revenue)
        ));
    }

    @ApiResponse(message = "This month's revenue retrieved successfully")
    @Operation(summary = "Doanh thu tháng hiện tại", description = "Lấy tổng doanh thu của tháng hiện tại")
    @GetMapping("/month")
    public ResponseEntity<ResponseObject> getThisMonthRevenue() {
        double revenue = dashboardService.getRevenueByThisMonth();
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "This month's revenue retrieved successfully",
                Map.of("revenue", revenue)
        ));
    }

    @ApiResponse(message = "This year's revenue retrieved successfully")
    @Operation(summary = "Doanh thu năm hiện tại", description = "Lấy tổng doanh thu của năm hiện tại")
    @GetMapping("/year")
    public ResponseEntity<ResponseObject> getThisYearRevenue() {
        double revenue = dashboardService.getRevenueByThisYear();
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "This year's revenue retrieved successfully",
                Map.of("revenue", revenue)
        ));
    }

    // ================================
    // Doanh thu theo ngày, tháng, năm chỉ định
    // ================================
    @ApiResponse(message = "Revenue by date retrieved successfully")
    @Operation(summary = "Doanh thu theo ngày chỉ định", description = "Lấy tổng doanh thu của ngày chỉ định")
    @GetMapping("/by-date")
    public ResponseEntity<ResponseObject> getRevenueByDate(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        double revenue = dashboardService.getRevenueByDate(date);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Revenue for " + date + " retrieved successfully",
                Map.of("revenue", revenue)
        ));
    }

    @ApiResponse(message = "Revenue by month retrieved successfully")
    @Operation(summary = "Doanh thu theo tháng chỉ định", description = "Lấy tổng doanh thu của tháng chỉ định")
    @GetMapping("/by-month")
    public ResponseEntity<ResponseObject> getRevenueByMonth(
            @RequestParam("year") int year,
            @RequestParam("month") int month
    ) {
        double revenue = dashboardService.getRevenueByMonth(year, month);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Revenue for " + month + "/" + year + " retrieved successfully",
                Map.of("revenue", revenue)
        ));
    }

    @ApiResponse(message = "Revenue by year retrieved successfully")
    @Operation(summary = "Doanh thu theo năm chỉ định", description = "Lấy tổng doanh thu của năm chỉ định")
    @GetMapping("/by-year")
    public ResponseEntity<ResponseObject> getRevenueByYear(
            @RequestParam("year") int year
    ) {
        double revenue = dashboardService.getRevenueByYear(year);
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Revenue for year " + year + " retrieved successfully",
                Map.of("revenue", revenue)
        ));
    }
}