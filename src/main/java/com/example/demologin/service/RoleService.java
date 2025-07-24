package com.example.demologin.service;

import com.example.demologin.dto.request.RoleRequestDTO;
import com.example.demologin.dto.request.RolePermissionsRequestDTO;
import com.example.demologin.entity.Role;
import java.util.List;

public interface RoleService {
    List<Role> getAll();
    Role create(RoleRequestDTO req);
    Role update(Long id, RoleRequestDTO req);
    void delete(Long id, RoleRequestDTO req);
    Role updatePermissions(Long id, RolePermissionsRequestDTO req);
} 