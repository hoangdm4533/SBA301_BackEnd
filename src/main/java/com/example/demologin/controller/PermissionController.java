package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.annotation.UserAction;
import com.example.demologin.dto.request.PermissionRequest;
import com.example.demologin.enums.UserActionType;
import com.example.demologin.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/permissions")
@Tag(name = "Permission Management", description = "APIs for managing system permissions")
    public class PermissionController {
    @Autowired private PermissionService permissionService;

    @SecuredEndpoint("PERMISSION_VIEW")
    @GetMapping
    @ApiResponse(message = "Permissions retrieved successfully")
    @Operation(summary = "Get all permissions", 
               description = "Retrieve all system permissions")
    public Object getAll() {
        return permissionService.getAll();
    }

    @UserAction(actionType = UserActionType.UPDATE, targetType = "PERMISSION",
               description = "Update permission name", requiresReason = true)
    @PutMapping("/{id}")
    @ApiResponse(message = "Permission updated successfully")
    @SecuredEndpoint("PERMISSION_UPDATE")
    @Operation(summary = "Update permission", 
               description = "Update permission name and description")
    public Object update(
            @Parameter(description = "Permission ID") @PathVariable Long id,
            @RequestBody @Valid PermissionRequest req) {
        return permissionService.updatePermissionName(id, req);
    }
} 
