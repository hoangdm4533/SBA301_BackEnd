package com.example.demologin.mapper.permission;

import com.example.demologin.dto.request.PermissionRequest;
import com.example.demologin.dto.response.PermissionResponse;
import com.example.demologin.entity.Permission;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PermissionMapper implements IPermissionMapper {
    @Override
    public void updateEntityFromDto(PermissionRequest dto, Permission entity) {
        if (dto == null || entity == null) return;
        entity.setName(dto.getName());
    }

    @Override
    public PermissionResponse toResponse(Permission entity) {
        if (entity == null) return null;
        return new PermissionResponse(entity.getId(), entity.getCode(), entity.getName());
    }

    @Override
    public List<PermissionResponse> toResponseList(List<Permission> entities) {
        if (entities == null) return List.of();
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
