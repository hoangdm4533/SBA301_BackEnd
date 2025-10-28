package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.dto.response.DashboardCardResponse;
import com.example.demologin.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard/card")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN')")
public class DashBoardCardController {

    private final DashboardService dashboardService;

    @ApiResponse
    @GetMapping
    public DashboardCardResponse getDashboardCard() {
        return dashboardService.dashboardCard();
    }
}
