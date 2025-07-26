package com.example.demologin.controller;

import com.example.demologin.annotation.RequirePermission;
import com.example.demologin.dto.request.AdminActionRequest;
import com.example.demologin.dto.request.role.CreateRoleRequest;
import com.example.demologin.dto.request.role.DeleteRoleRequest;
import com.example.demologin.dto.request.role.RolePermissionsRequest;
import com.example.demologin.dto.request.role.UpdateRoleRequest;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.service.RoleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/roles")
@SecurityRequirement(name = "api")
public class RoleController {
    @Autowired private RoleService roleService;

    @RequirePermission("ROLE_VIEW")
    @GetMapping
    public ResponseEntity<ResponseObject> getAll() {
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Success", roleService.getAll()));
    }

    @RequirePermission("ROLE_CREATE")
    @PostMapping
    public ResponseEntity<ResponseObject> create(@RequestBody @Valid CreateRoleRequest req) {
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Success", roleService.create(req)));
    }

    @RequirePermission("ROLE_UPDATE")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> update(@PathVariable Long id, @RequestBody @Valid UpdateRoleRequest req) {
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Success", roleService.update(id, req)));
    }

    @RequirePermission("ROLE_DELETE")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> delete(@PathVariable Long id, @RequestBody @Valid DeleteRoleRequest req) {
        roleService.delete(id, req);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Deleted", null));
    }

    @RequirePermission("ROLE_UPDATE_PERMISSIONS")
    @PutMapping("/{id}/permissions")
    public ResponseEntity<ResponseObject> updatePermissions(@PathVariable Long id, @RequestBody @Valid RolePermissionsRequest req) {
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Success", roleService.updatePermissions(id, req)));
    }
}
