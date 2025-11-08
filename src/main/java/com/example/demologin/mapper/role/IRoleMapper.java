package com.example.demologin.mapper.role;

import com.example.demologin.dto.request.role.CreateRoleRequest;
import com.example.demologin.dto.request.role.RolePermissionsRequest;
import com.example.demologin.dto.request.role.UpdateRoleRequest;
import com.example.demologin.dto.response.RoleResponse;
import com.example.demologin.entity.Role;

import java.util.List;

public interface IRoleMapper {
    void fromCreateDto(CreateRoleRequest dto, Role entity);
    void fromUpdateDto(UpdateRoleRequest dto, Role entity);
    void fromPermissionDto(RolePermissionsRequest dto, Role entity);
    RoleResponse toResponse(Role entity);
    List<RoleResponse> toResponseList(List<Role> entities);
}
