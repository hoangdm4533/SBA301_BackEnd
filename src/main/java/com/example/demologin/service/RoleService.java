package com.example.demologin.service;

import com.example.demologin.dto.request.role.CreateRoleRequest;
import com.example.demologin.dto.request.role.DeleteRoleRequest;
import com.example.demologin.dto.request.role.UpdateRoleRequest;
import com.example.demologin.dto.request.role.RolePermissionsRequest;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.entity.Role;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface RoleService {
    ResponseEntity<ResponseObject> getAll();

    ResponseEntity<ResponseObject> create(CreateRoleRequest req);

    ResponseEntity<ResponseObject> update(Long id, UpdateRoleRequest req);

    ResponseEntity<ResponseObject> delete(Long id, DeleteRoleRequest req);

    ResponseEntity<ResponseObject> updatePermissions(Long id, RolePermissionsRequest req);
}
