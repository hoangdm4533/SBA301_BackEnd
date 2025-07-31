package com.example.demologin.controller;

import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.annotation.UserAction;
import com.example.demologin.dto.request.role.CreateRoleRequest;
import com.example.demologin.dto.request.role.DeleteRoleRequest;
import com.example.demologin.dto.request.role.RolePermissionsRequest;
import com.example.demologin.dto.request.role.UpdateRoleRequest;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.enums.UserActionType;
import com.example.demologin.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/roles")
@Tag(name = "Role Management", description = "APIs for managing user roles and role permissions")
    public class RoleController {
    @Autowired private RoleService roleService;

    @SecuredEndpoint("ROLE_VIEW")
    @GetMapping
    @Operation(summary = "Get all roles", 
               description = "Retrieve all roles in the system")
    public ResponseEntity<ResponseObject> getAll() {
        return roleService.getAll();
    }

    @UserAction(actionType = UserActionType.CREATE, targetType = "ROLE", 
               description = "Create new role", requiresReason = true)
    @PostMapping
    @SecuredEndpoint("ROLE_CREATE")
    @Operation(summary = "Create new role", 
               description = "Create a new role with specified name and description")
    public ResponseEntity<ResponseObject> create(@RequestBody @Valid CreateRoleRequest req) {
        return roleService.create(req);
    }

    @UserAction(actionType = UserActionType.UPDATE, targetType = "ROLE", 
               description = "Update role information", requiresReason = true)
    @PutMapping("/{id}")
    @SecuredEndpoint("ROLE_UPDATE")
    @Operation(summary = "Update role", 
               description = "Update role name and description")
    public ResponseEntity<ResponseObject> update(
            @Parameter(description = "Role ID") @PathVariable Long id, 
            @RequestBody @Valid UpdateRoleRequest req) {
        return roleService.update(id, req);
    }

    @UserAction(actionType = UserActionType.DELETE, targetType = "ROLE", 
               description = "Delete role", requiresReason = true)
    @DeleteMapping("/{id}")
    @SecuredEndpoint("ROLE_DELETE")
    @Operation(summary = "Delete role", 
               description = "Delete a role from the system")
    public ResponseEntity<ResponseObject> delete(
            @Parameter(description = "Role ID") @PathVariable Long id, 
            @RequestBody @Valid DeleteRoleRequest req) {
        return roleService.delete(id, req);
    }

    @UserAction(actionType = UserActionType.UPDATE, targetType = "ROLE", 
               description = "Update role permissions", requiresReason = true)
    @PutMapping("/{id}/permissions")
    @SecuredEndpoint("ROLE_UPDATE_PERMISSIONS")
    @Operation(summary = "Update role permissions", 
               description = "Update permissions assigned to a role")
    public ResponseEntity<ResponseObject> updatePermissions(
            @Parameter(description = "Role ID") @PathVariable Long id, 
            @RequestBody @Valid RolePermissionsRequest req) {
        return roleService.updatePermissions(id, req);
    }
}
