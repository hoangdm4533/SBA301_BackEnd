package com.example.demologin.controller;

import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.annotation.UserActivity;
import com.example.demologin.dto.request.PermissionRequest;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.enums.ActivityType;
import com.example.demologin.service.PermissionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/admin/permissions")
public class PermissionController {
    @Autowired private PermissionService permissionService;

    @SecuredEndpoint("PERMISSION_VIEW")
    @GetMapping
    public ResponseEntity<ResponseObject> getAll() {
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Success", permissionService.getAll()));
    }

    @SecuredEndpoint("PERMISSION_UPDATE")
    @PutMapping("/{id}")
    @UserActivity(activityType = ActivityType.ADMIN_ACTION, details = "Update permission name")
    public ResponseEntity<ResponseObject> update(
            @PathVariable Long id,
            @RequestBody @Valid PermissionRequest req) {
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Success", permissionService.updatePermissionName(id, req)));
    }
} 