package com.example.demologin.controller;

import com.example.demologin.annotation.RequirePermission;
import com.example.demologin.dto.request.PermissionRequest;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.service.PermissionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SecurityRequirement(name = "api")
@RestController
@RequestMapping("/api/admin/permissions")
public class PermissionController {
    @Autowired private PermissionService permissionService;

    @RequirePermission("PERMISSION_VIEW")
    @GetMapping
    public ResponseEntity<ResponseObject> getAll() {
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Success", permissionService.getAll()));
    }

    @RequirePermission("PERMISSION_UPDATE")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> update(
            @PathVariable Long id,
            @RequestBody @Valid PermissionRequest req) {
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Success", permissionService.updatePermissionName(id, req)));
    }
} 