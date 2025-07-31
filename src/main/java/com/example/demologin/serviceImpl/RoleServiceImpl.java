package com.example.demologin.serviceImpl;

import com.example.demologin.annotation.UserAction;
import com.example.demologin.enums.UserActionType;
import com.example.demologin.dto.request.role.CreateRoleRequest;
import com.example.demologin.dto.request.role.DeleteRoleRequest;
import com.example.demologin.dto.request.role.RolePermissionsRequest;
import com.example.demologin.dto.request.role.UpdateRoleRequest;
import com.example.demologin.dto.response.ResponseObject;
import com.example.demologin.entity.Role;
import com.example.demologin.exception.exceptions.BusinessException;
import com.example.demologin.exception.exceptions.NotFoundException;
import com.example.demologin.mapper.RoleMapper;
import com.example.demologin.repository.RoleRepository;
import com.example.demologin.repository.UserRepository;
import com.example.demologin.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired private RoleRepository roleRepository;
    @Autowired private RoleMapper roleMapper;
    @Autowired private UserRepository userRepository;

    @Override
    public ResponseEntity<ResponseObject> getAll() {
        List<Role> roles = roleRepository.findAll();
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Success", roles));
    }

    @Override
    @UserAction(actionType = UserActionType.CREATE, requiresReason = true, 
               description = "Create new role")
    public ResponseEntity<ResponseObject> create(CreateRoleRequest req) {
        if (roleRepository.existsByName(req.name)) {
            throw new BusinessException("Role name already exists");
        }

        Role role = new Role();
        roleMapper.fromCreateDto(req, role);
        Role savedRole = roleRepository.save(role);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Success", savedRole));
    }

    @Override
    @UserAction(actionType = UserActionType.UPDATE, requiresReason = true,
               description = "Update role information")
    public ResponseEntity<ResponseObject> update(Long id, UpdateRoleRequest req) {
        Role r = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role with id " + id + " not found"));
        roleMapper.fromUpdateDto(req, r);
        Role updatedRole = roleRepository.save(r);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Success", updatedRole));
    }

    @Override
    @UserAction(actionType = UserActionType.DELETE, requiresReason = true,
               description = "Delete role")
    public ResponseEntity<ResponseObject> delete(Long id, DeleteRoleRequest req) {
        Role r = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role with id " + id + " not found"));

        if (userRepository.existsByRoles_Id(id)) {
            throw new BusinessException("Role is assigned to users. Cannot delete.");
        }

        roleRepository.delete(r);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Deleted", null));
    }

    @Override
    @UserAction(actionType = UserActionType.UPDATE, requiresReason = true,
               description = "Update role permissions")
    public ResponseEntity<ResponseObject> updatePermissions(Long id, RolePermissionsRequest req) {
        Role r = roleRepository.findById(id).orElseThrow(() -> new NotFoundException("Role with id " + id + " not found"));
        roleMapper.fromPermissionDto(req, r);
        Role updatedRole = roleRepository.save(r);
        return ResponseEntity.ok(new ResponseObject(HttpStatus.OK.value(), "Success", updatedRole));
    }
}
