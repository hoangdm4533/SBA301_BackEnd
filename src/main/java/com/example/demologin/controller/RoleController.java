package com.example.demologin.controller;

import com.example.demologin.annotation.SecuredEndpoint;
import com.example.demologin.annotation.UserActivity;
import com.example.demologin.dto.request.role.CreateRoleRequest;
import com.example.demologin.dto.request.role.DeleteRoleRequest;
import com.example.demologin.dto.request.role.RolePermissionsRequest;
import com.example.demologin.dto.request.role.UpdateRoleRequest;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.enums.ActivityType;
import com.example.demologin.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/roles")
public class RoleController {
    @Autowired private RoleService roleService;

    @SecuredEndpoint("ROLE_VIEW")
    @GetMapping
    public ResponseEntity<ResponseObject> getAll() {
        return roleService.getAll();
    }

    @SecuredEndpoint("ROLE_CREATE")
    @PostMapping
    @UserActivity(activityType = ActivityType.ADMIN_ACTION, details = "Create new role")
    public ResponseEntity<ResponseObject> create(@RequestBody @Valid CreateRoleRequest req) {
        return roleService.create(req);
    }

    @SecuredEndpoint("ROLE_UPDATE")
    @PutMapping("/{id}")
    @UserActivity(activityType = ActivityType.ADMIN_ACTION, details = "Update role information")
    public ResponseEntity<ResponseObject> update(@PathVariable Long id, @RequestBody @Valid UpdateRoleRequest req) {
        return roleService.update(id, req);
    }

    @SecuredEndpoint("ROLE_DELETE")
    @DeleteMapping("/{id}")
    @UserActivity(activityType = ActivityType.ADMIN_ACTION, details = "Delete role")
    public ResponseEntity<ResponseObject> delete(@PathVariable Long id, @RequestBody @Valid DeleteRoleRequest req) {
        return roleService.delete(id, req);
    }

    @SecuredEndpoint("ROLE_UPDATE_PERMISSIONS")
    @PutMapping("/{id}/permissions")
    @UserActivity(activityType = ActivityType.ADMIN_ACTION, details = "Update role permissions")
    public ResponseEntity<ResponseObject> updatePermissions(@PathVariable Long id, @RequestBody @Valid RolePermissionsRequest req) {
        return roleService.updatePermissions(id, req);
    }
}
