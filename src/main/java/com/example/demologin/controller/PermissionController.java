package com.example.demologin.controller;

import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.annotation.UserAction;
import com.example.demologin.dto.request.PermissionRequest;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.enums.UserActionType;
import com.example.demologin.service.PermissionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/admin/permissions")
public class PermissionController {
    @Autowired private PermissionService permissionService;

    @SecuredEndpoint("PERMISSION_VIEW")
    @GetMapping
    public ResponseEntity<ResponseObject> getAll() {
        return permissionService.getAll();
    }

    @UserAction(actionType = UserActionType.UPDATE, targetType = "PERMISSION", 
               description = "Update permission name", requiresReason = true)
    @PutMapping("/{id}")
    @SecuredEndpoint("PERMISSION_UPDATE")
    public ResponseEntity<ResponseObject> update(
            @PathVariable Long id,
            @RequestBody @Valid PermissionRequest req) {
        return permissionService.updatePermissionName(id, req);
    }
} 