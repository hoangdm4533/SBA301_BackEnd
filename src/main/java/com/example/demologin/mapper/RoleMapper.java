package com.example.demologin.mapper;

import com.example.demologin.dto.request.role.CreateRoleRequest;
import com.example.demologin.dto.request.role.DeleteRoleRequest;
import com.example.demologin.dto.request.role.RolePermissionsRequest;
import com.example.demologin.dto.request.role.UpdateRoleRequest;
import com.example.demologin.entity.Role;
import com.example.demologin.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashSet;

@Component
public class RoleMapper {
    @Autowired private PermissionRepository permissionRepository;

    public void fromCreateDto(CreateRoleRequest dto, Role entity) {
        entity.setName(dto.name);
    }

    public void fromDeleteDto(DeleteRoleRequest dto) {
    }


    public void fromUpdateDto(UpdateRoleRequest dto, Role entity) {
        entity.setName(dto.name);
    }

    public void fromPermissionDto(RolePermissionsRequest dto, Role entity) {
        if (dto.permissionIds != null) {
            entity.setPermissions(new HashSet<>(permissionRepository.findAllById(dto.permissionIds)));
        }
    }
}
