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

    @UserAction(actionType = UserActionType.CREATE, targetType = "ROLE", 
               description = "Create new role", requiresReason = true)
    @PostMapping
    @SecuredEndpoint("ROLE_CREATE")
    public ResponseEntity<ResponseObject> create(@RequestBody @Valid CreateRoleRequest req) {
        return roleService.create(req);
    }

    @UserAction(actionType = UserActionType.UPDATE, targetType = "ROLE", 
               description = "Update role information", requiresReason = true)
    @PutMapping("/{id}")
    @SecuredEndpoint("ROLE_UPDATE")
    public ResponseEntity<ResponseObject> update(@PathVariable Long id, @RequestBody @Valid UpdateRoleRequest req) {
        return roleService.update(id, req);
    }

    @UserAction(actionType = UserActionType.DELETE, targetType = "ROLE", 
               description = "Delete role", requiresReason = true)
    @DeleteMapping("/{id}")
    @SecuredEndpoint("ROLE_DELETE")
    public ResponseEntity<ResponseObject> delete(@PathVariable Long id, @RequestBody @Valid DeleteRoleRequest req) {
        return roleService.delete(id, req);
    }

    @UserAction(actionType = UserActionType.UPDATE, targetType = "ROLE", 
               description = "Update role permissions", requiresReason = true)
    @PutMapping("/{id}/permissions")
    @SecuredEndpoint("ROLE_UPDATE_PERMISSIONS")
    public ResponseEntity<ResponseObject> updatePermissions(@PathVariable Long id, @RequestBody @Valid RolePermissionsRequest req) {
        return roleService.updatePermissions(id, req);
    }
}
