package com.example.demologin.serviceImpl;

import com.example.demologin.annotation.UserAction;
import com.example.demologin.enums.UserActionType;
import com.example.demologin.entity.Permission;
import com.example.demologin.exception.exceptions.NotFoundException;
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
    @UserAction(actionType = UserActionType.UPDATE, requiresReason = true,
               description = "Update permission name")
    public Permission updatePermissionName(Long id, PermissionRequest req) {
        Permission p = permissionRepository.findById(id).orElseThrow(() -> new NotFoundException("Permission with id " + id + " not found"));
        permissionMapper.updateEntityFromDto(req, p);
        return permissionRepository.save(p);
    }
} 