package com.example.demologin.mapper.role;

import com.example.demologin.dto.request.role.CreateRoleRequest;
import com.example.demologin.dto.request.role.RolePermissionsRequest;
import com.example.demologin.dto.request.role.UpdateRoleRequest;
import com.example.demologin.dto.response.PermissionResponse;
import com.example.demologin.dto.response.RoleResponse;
import com.example.demologin.entity.Role;
import com.example.demologin.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoleMapper implements IRoleMapper {
    private final PermissionRepository permissionRepository;

    @Override
    public void fromCreateDto(CreateRoleRequest dto, Role entity) {
        if (dto == null || entity == null) return;
        entity.setName(dto.name);
    }

    @Override
    public void fromUpdateDto(UpdateRoleRequest dto, Role entity) {
        if (dto == null || entity == null) return;
        entity.setName(dto.name);
    }

    @Override
    public void fromPermissionDto(RolePermissionsRequest dto, Role entity) {
        if (dto == null || entity == null) return;
        if (dto.permissionIds != null) {
            entity.setPermissions(new HashSet<>(permissionRepository.findAllById(dto.permissionIds)));
        }
    }

    @Override
    public RoleResponse toResponse(Role entity) {
        if (entity == null) return null;

        Set<PermissionResponse> permissions =
                entity.getPermissions() == null ? Set.of()
                        : entity.getPermissions().stream()
                        .map(p -> new PermissionResponse(p.getId(), p.getCode(), p.getName()))
                        .collect(Collectors.toSet());

        return new RoleResponse(entity.getId(), entity.getName(), permissions);
    }

    @Override
    public List<RoleResponse> toResponseList(List<Role> entities) {
        if (entities == null) return List.of();
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

}
