package com.example.demologin.service;

import com.example.demologin.dto.request.PermissionRequest;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.entity.Permission;
import org.springframework.http.ResponseEntity;
import java.util.List;

public interface PermissionService {
    ResponseEntity<ResponseObject> getAll();
    ResponseEntity<ResponseObject> updatePermissionName(Long id, PermissionRequest req);
} 