package com.example.demologin.mapper;

import com.example.demologin.dto.request.RoleRequestDTO;
import com.example.demologin.dto.request.RolePermissionsRequestDTO;
import com.example.demologin.entity.Role;
import com.example.demologin.entity.Permission;
import com.example.demologin.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashSet;

@Component
public class RoleMapper {
    @Autowired private PermissionRepository permissionRepository;

    public void updateEntityFromDto(RoleRequestDTO dto, Role entity) {
        entity.setName(dto.name);
        if (dto.permissionIds != null) {
            entity.setPermissions(new HashSet<>(permissionRepository.findAllById(dto.permissionIds)));
        }
    }
    public void updatePermissionsFromDto(RolePermissionsRequestDTO dto, Role entity) {
        if (dto.permissionIds != null) {
            entity.setPermissions(new HashSet<>(permissionRepository.findAllById(dto.permissionIds)));
        }
    }
} 