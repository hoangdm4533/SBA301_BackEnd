package com.example.demologin.controller;

import com.example.demologin.annotation.RequirePermission;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.service.AdminActionLogService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<ResponseObject> getAllLogs(
            @RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "10") int size) {
        ResponseObject response = adminActionLogService.getAllLogs(page, size);
        return ResponseEntity.ok(response);
    }
}
