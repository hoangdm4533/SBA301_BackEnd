package com.example.demologin.service;

import com.example.demologin.dto.request.PermissionRequest;
import com.example.demologin.entity.Permission;
import java.util.List;

public interface PermissionService {
    List<Permission> getAll();
    Permission updatePermissionName(Long id, PermissionRequest req);
} 