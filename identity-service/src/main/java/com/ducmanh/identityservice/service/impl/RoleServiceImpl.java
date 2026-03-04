package com.ducmanh.identityservice.service.impl;

import com.ducmanh.identityservice.dto.request.RoleRequest;
import com.ducmanh.identityservice.dto.response.RoleResponse;
import com.ducmanh.identityservice.entity.Permission;
import com.ducmanh.identityservice.entity.Role;
import com.ducmanh.identityservice.mapper.RoleMapper;
import com.ducmanh.identityservice.repository.PermissionRepository;
import com.ducmanh.identityservice.repository.RoleRepository;
import com.ducmanh.identityservice.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final PermissionRepository permissionRepository;


    @Override
    public RoleResponse create(RoleRequest request) {
        Role role = roleMapper.toRole(request);

        List<Permission> permissions= permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));

        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    @Override
    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream().map(roleMapper::toRoleResponse).toList();
    }

    @Override
    public void delete(String role) {
        roleRepository.deleteById(role);
    }
}
