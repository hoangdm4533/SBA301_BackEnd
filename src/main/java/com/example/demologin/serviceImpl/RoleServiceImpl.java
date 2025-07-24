package com.example.demologin.serviceImpl;

import com.example.demologin.annotation.AdminAction;
import com.example.demologin.dto.request.RoleRequestDTO;
import com.example.demologin.dto.request.RolePermissionsRequestDTO;
import com.example.demologin.entity.Role;
import com.example.demologin.mapper.RoleMapper;
import com.example.demologin.repository.RoleRepository;
import com.example.demologin.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired private RoleRepository roleRepository;
    @Autowired private RoleMapper roleMapper;

    @Override
    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    @Override
    @AdminAction(action = "CREATE", entity = "ROLE", reasonRequired = "true")
    public Role create(RoleRequestDTO req) {
        Role role = new Role();
        roleMapper.updateEntityFromDto(req, role);
        return roleRepository.save(role);
    }

    @Override
    @AdminAction(action = "UPDATE", entity = "ROLE", reasonRequired = "true")
    public Role update(Long id, RoleRequestDTO req) {
        Role r = roleRepository.findById(id).orElseThrow();
        roleMapper.updateEntityFromDto(req, r);
        return roleRepository.save(r);
    }

    @Override
    @AdminAction(action = "DELETE", entity = "ROLE", reasonRequired = "true")
    public void delete(Long id, RoleRequestDTO req) {
        roleRepository.deleteById(id);
    }

    @Override
    @AdminAction(action = "UPDATE_PERMISSIONS", entity = "ROLE", reasonRequired = "true")
    public Role updatePermissions(Long id, RolePermissionsRequestDTO req) {
        Role r = roleRepository.findById(id).orElseThrow();
        roleMapper.updatePermissionsFromDto(req, r);
        return roleRepository.save(r);
    }
} 