package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.dto.response.DashboardCardResponse;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard/card")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN')")
@Tag(name = "Dashboard Card", description = "Dashboard card statistics endpoint")
public class DashBoardCardController {

    private final DashboardService dashboardService;

    @ApiResponse(message = "Dashboard card retrieved successfully")
    @Operation(summary = "Get dashboard card", description = "Retrieve dashboard card statistics including revenue, users, and subscriptions")
    @GetMapping
    public ResponseEntity<ResponseObject> getDashboardCard() {
        final DashboardCardResponse data = dashboardService.dashboardCard();
        return ResponseEntity.ok(new ResponseObject(
                HttpStatus.OK.value(),
                "Dashboard card retrieved successfully",
                data
        ));
    }
}
