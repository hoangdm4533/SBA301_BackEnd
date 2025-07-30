package com.example.demologin.serviceImpl;

import com.example.demologin.annotation.UserAction;
import com.example.demologin.enums.UserActionType;
import com.example.demologin.dto.request.role.CreateRoleRequest;
import com.example.demologin.dto.request.role.DeleteRoleRequest;
import com.example.demologin.dto.request.role.RolePermissionsRequest;
import com.example.demologin.dto.request.role.UpdateRoleRequest;
import com.example.demologin.entity.Role;
import com.example.demologin.exception.exceptions.BusinessException;
import com.example.demologin.exception.exceptions.NotFoundException;
import com.example.demologin.mapper.RoleMapper;
import com.example.demologin.repository.RoleRepository;
import com.example.demologin.repository.UserRepository;
import com.example.demologin.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired private RoleRepository roleRepository;
    @Autowired private RoleMapper roleMapper;
    @Autowired private UserRepository userRepository;

    @Override
    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    @Override
    @UserAction(actionType = UserActionType.CREATE, requiresReason = true, 
               description = "Create new role")
    public Role create(CreateRoleRequest req) {
        if (roleRepository.existsByName(req.name)) {
            throw new BusinessException("Role name already exists");
        }

        Role role = new Role();
        roleMapper.fromCreateDto(req, role);
        return roleRepository.save(role);
    }

    @Override
    @UserAction(actionType = UserActionType.UPDATE, requiresReason = true,
               description = "Update role information")
    public Role update(Long id, UpdateRoleRequest req) {
        Role r = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role with id " + id + " not found"));
        roleMapper.fromUpdateDto(req, r);
        return roleRepository.save(r);
    }

    @Override
    @UserAction(actionType = UserActionType.DELETE, requiresReason = true,
               description = "Delete role")
    public void delete(Long id, DeleteRoleRequest req) {
        Role r = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role with id " + id + " not found"));

        if (userRepository.existsByRoles_Id(id)) {
            throw new BusinessException("Role is assigned to users. Cannot delete.");
        }

        roleRepository.delete(r);
    }

    @Override
    @UserAction(actionType = UserActionType.UPDATE, requiresReason = true,
               description = "Update role permissions")
    public Role updatePermissions(Long id, RolePermissionsRequest req) {
        Role r = roleRepository.findById(id).orElseThrow(() -> new NotFoundException("Role with id " + id + " not found"));
        roleMapper.fromPermissionDto(req, r);
        return roleRepository.save(r);
    }
}
