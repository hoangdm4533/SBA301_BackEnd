package com.example.demologin.serviceImpl;

import com.example.demologin.annotation.UserAction;
import com.example.demologin.enums.UserActionType;
import com.example.demologin.entity.Permission;
import com.example.demologin.exception.exceptions.NotFoundException;
import com.example.demologin.repository.PermissionRepository;
import com.example.demologin.dto.request.PermissionRequest;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.mapper.PermissionMapper;
import com.example.demologin.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {
    @Autowired private PermissionRepository permissionRepository;
    @Autowired private PermissionMapper permissionMapper;

    @Override
    public ResponseEntity<ResponseObject> getAll() {
        List<Permission> permissions = permissionRepository.findAll();
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Success", permissions));
    }

    @Override
    @UserAction(actionType = UserActionType.UPDATE, requiresReason = true,
               description = "Update permission name")
    public ResponseEntity<ResponseObject> updatePermissionName(Long id, PermissionRequest req) {
        Permission p = permissionRepository.findById(id).orElseThrow(() -> new NotFoundException("Permission with id " + id + " not found"));
        permissionMapper.updateEntityFromDto(req, p);
        Permission updatedPermission = permissionRepository.save(p);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Success", updatedPermission));
    }
} 