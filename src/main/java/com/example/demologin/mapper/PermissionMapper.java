package com.example.demologin.mapper;

import com.example.demologin.dto.request.PermissionRequestDTO;
import com.example.demologin.entity.Permission;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper {
    public void updateEntityFromDto(PermissionRequestDTO dto, Permission entity) {
        entity.setName(dto.name);
    }
} 