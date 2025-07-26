package com.example.demologin.controller;

import com.example.demologin.annotation.RequirePermission;
import com.example.demologin.dto.response.AdminActionLogResponse;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.service.AdminActionLogService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/action-logs")
@SecurityRequirement(name = "api")
public class AdminActionLogController {

    @Autowired
    private AdminActionLogService adminActionLogService;

    @RequirePermission("ADMIN_ACTION_LOG_VIEW")
    @GetMapping
    public Object getAllLogs(
            @PageableDefault(page = 0, size = 10, sort = "actionTime", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return adminActionLogService.getLogs(pageable);
    }
}
