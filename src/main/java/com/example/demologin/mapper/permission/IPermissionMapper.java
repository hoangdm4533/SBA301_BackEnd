package com.example.demologin.mapper.permission;

import com.example.demologin.dto.request.PermissionRequest;
import com.example.demologin.dto.response.PermissionResponse;
import com.example.demologin.entity.Permission;

import java.util.List;

public interface IPermissionMapper {
    void updateEntityFromDto(PermissionRequest dto, Permission entity);
    PermissionResponse toResponse(Permission entity);
    List<PermissionResponse> toResponseList(List<Permission> entities);
}
