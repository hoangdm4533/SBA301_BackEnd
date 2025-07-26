package com.example.demologin.serviceImpl;

import com.example.demologin.annotation.AdminAction;
import com.example.demologin.entity.Permission;
import com.example.demologin.repository.PermissionRepository;
import com.example.demologin.dto.request.PermissionRequest;
import com.example.demologin.mapper.PermissionMapper;
import com.example.demologin.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {
    @Autowired private PermissionRepository permissionRepository;
    @Autowired private PermissionMapper permissionMapper;

    @Override
    public List<Permission> getAll() {
        return permissionRepository.findAll();
    }

    @Override
    @AdminAction(action = "UPDATE", entity = "PERMISSION", reasonRequired = "true")
    public Permission updatePermissionName(Long id, PermissionRequest req) {
        Permission p = permissionRepository.findById(id).orElseThrow();
        permissionMapper.updateEntityFromDto(req, p);
        return permissionRepository.save(p);
    }
} 