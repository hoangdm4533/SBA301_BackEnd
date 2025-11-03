package com.example.demologin.controller;

import com.example.demologin.annotation.ApiResponse;
import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.annotation.SmartCache;
import com.example.demologin.dto.request.role.CreateRoleRequest;
import com.example.demologin.dto.request.role.RolePermissionsRequest;
import com.example.demologin.dto.request.role.UpdateRoleRequest;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/roles")
@Tag(name = "Role Management", description = "APIs for managing user roles and role permissions")
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @SmartCache
    @ApiResponse(message = "Roles retrieved successfully")
    @Operation(summary = "Get all roles", description = "Retrieve all roles in the system")
    public ResponseEntity<ResponseObject> getAll() {
        final var data = roleService.getAll();
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Roles retrieved successfully", data));
    }

    @PostMapping
    @SmartCache
    @SecuredEndpoint("ROLE_CREATE")
    @ApiResponse(message = "Role created successfully")
    @Operation(summary = "Create new role", description = "Create a new role with specified name and description")
    public ResponseEntity<ResponseObject> create(@RequestBody @Valid final CreateRoleRequest req) {
        final var data = roleService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseObject(HttpStatus.CREATED.value(), "Role created successfully", data));
    }

    @PutMapping("/{id}")
    @SmartCache
    @SecuredEndpoint("ROLE_UPDATE")
    @ApiResponse(message = "Role updated successfully")
    @Operation(summary = "Update role", description = "Update role name and description")
    public ResponseEntity<ResponseObject> update(
            @Parameter(description = "Role ID") @PathVariable final Long id,
            @RequestBody @Valid final UpdateRoleRequest req) {
        final var data = roleService.update(id, req);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Role updated successfully", data));
    }

    @DeleteMapping("/{id}")
    @SmartCache
    @SecuredEndpoint("ROLE_DELETE")
    @ApiResponse(message = "Role deleted successfully")
    @Operation(summary = "Delete role", description = "Delete a role from the system")
    public ResponseEntity<ResponseObject> delete(@PathVariable final Long id) {
        roleService.delete(id);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Role deleted successfully", id));
    }

    @PutMapping("/{id}/permissions")
    @SmartCache
    @SecuredEndpoint("ROLE_PERMISSION_UPDATE")
    @ApiResponse(message = "Role permissions updated successfully")
    @Operation(summary = "Update role permissions", description = "Update permissions assigned to a role")
    public ResponseEntity<ResponseObject> updatePermissions(
            @Parameter(description = "Role ID") @PathVariable final Long id,
            @RequestBody @Valid final RolePermissionsRequest req) {
        final var data = roleService.updatePermissions(id, req);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Role permissions updated successfully", data));
    }

    @GetMapping("/{id}")
    @SmartCache
    @SecuredEndpoint("ROLE_VIEW")
    @ApiResponse(message = "Role retrieved successfully")
    @Operation(summary = "Get role by ID", description = "Retrieve a role by its ID")
    public ResponseEntity<ResponseObject> getById(
            @Parameter(description = "Role ID") @PathVariable final Long id) {
        final var data = roleService.getById(id);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Role retrieved successfully", data));
    }
}
