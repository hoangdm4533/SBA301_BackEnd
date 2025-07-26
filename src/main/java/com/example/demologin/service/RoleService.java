package com.example.demologin.service;

import com.example.demologin.dto.request.AdminActionRequest;
import com.example.demologin.dto.request.role.CreateRoleRequest;
import com.example.demologin.dto.request.role.DeleteRoleRequest;
import com.example.demologin.dto.request.role.UpdateRoleRequest;
import com.example.demologin.dto.request.role.RolePermissionsRequest;
import com.example.demologin.entity.Role;

import java.util.List;

public interface RoleService {
    List<Role> getAll();

    Role create(CreateRoleRequest req);

    Role update(Long id, UpdateRoleRequest req);

    void delete(Long id, DeleteRoleRequest req);

    Role updatePermissions(Long id, RolePermissionsRequest req);
}
