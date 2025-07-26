package com.example.demologin.mapper;

import com.example.demologin.dto.request.PermissionRequest;
import com.example.demologin.entity.Permission;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper {
    public void updateEntityFromDto(PermissionRequest dto, Permission entity) {
        entity.setName(dto.getName());
    }
}
