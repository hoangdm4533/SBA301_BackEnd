package com.example.demologin.controller;

import com.example.demologin.annotation.AdminAction;
import com.example.demologin.annotation.RequirePermission;
import com.example.demologin.dto.request.RoleRequestDTO;
import com.example.demologin.dto.request.RolePermissionsRequestDTO;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.entity.Role;
import com.example.demologin.entity.Permission;
import com.example.demologin.repository.RoleRepository;
import com.example.demologin.repository.PermissionRepository;
import com.example.demologin.service.RoleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

@SecurityRequirement(name = "api")
@RestController
@RequestMapping("/api/admin/roles")
public class RoleController {
    @Autowired private RoleRepository roleRepository;
    @Autowired private PermissionRepository permissionRepository;
    @Autowired private RoleService roleService;

    @RequirePermission("ROLE_VIEW")
    @GetMapping
    public ResponseEntity<ResponseObject> getAll() {
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Success", roleService.getAll()));
    }

    @RequirePermission("ROLE_CREATE")
    @AdminAction(action = "CREATE", entity = "ROLE", reasonRequired = "true")
    @PostMapping
    public ResponseEntity<ResponseObject> create(@RequestBody RoleRequestDTO req) {
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Success", roleService.create(req)));
    }

    @RequirePermission("ROLE_UPDATE")
    @AdminAction(action = "UPDATE", entity = "ROLE", reasonRequired = "true")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> update(@PathVariable Long id, @RequestBody RoleRequestDTO req) {
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Success", roleService.update(id, req)));
    }

    @RequirePermission("ROLE_DELETE")
    @AdminAction(action = "DELETE", entity = "ROLE", reasonRequired = "true")
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> delete(@PathVariable Long id, @RequestBody RoleRequestDTO req) {
        roleService.delete(id, req);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Deleted", null));
    }

    @RequirePermission("ROLE_UPDATE_PERMISSIONS")
    @AdminAction(action = "UPDATE_PERMISSIONS", entity = "ROLE", reasonRequired = "true")
    @PutMapping("/{id}/permissions")
    public ResponseEntity<ResponseObject> updatePermissions(@PathVariable Long id, @RequestBody RolePermissionsRequestDTO req) {
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Success", roleService.updatePermissions(id, req)));
    }
} 