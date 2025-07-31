package com.example.demologin.serviceImpl;

import com.example.demologin.entity.Permission;
import com.example.demologin.exception.exceptions.NotFoundException;
import com.example.demologin.repository.PermissionRepository;
import com.example.demologin.dto.request.PermissionRequest;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.mapper.PermissionMapper;
import com.example.demologin.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    
    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @Override
    public ResponseEntity<ResponseObject> getAll() {
        List<Permission> permissions = permissionRepository.findAll();
        if (permissions.isEmpty()) {
            throw new NotFoundException("No permissions found");
        }
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Success", permissions));
    }

    @Override
    public ResponseEntity<ResponseObject> updatePermissionName(Long id, PermissionRequest req) {
        Permission p = permissionRepository.findById(id).orElseThrow(() -> new NotFoundException("Permission with id " + id + " not found"));
        permissionMapper.updateEntityFromDto(req, p);
        Permission updatedPermission = permissionRepository.save(p);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Success", updatedPermission));
    }
} 
