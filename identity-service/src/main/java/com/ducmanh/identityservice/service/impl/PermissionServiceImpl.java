package com.ducmanh.identityservice.service.impl;

import com.ducmanh.identityservice.dto.request.PermissionRequest;
import com.ducmanh.identityservice.dto.response.PermissionResponse;
import com.ducmanh.identityservice.entity.Permission;
import com.ducmanh.identityservice.mapper.PermissionMapper;
import com.ducmanh.identityservice.repository.PermissionRepository;
import com.ducmanh.identityservice.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @Override
    public PermissionResponse create(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);

        permission = permissionRepository.save(permission);

        return permissionMapper.toPermissionResponse(permission);
    }

    @Override
    public List<PermissionResponse> getAll() {
        List<Permission> permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
    }

    @Override
    public void delete(String permission) {
        permissionRepository.deleteById(permission);
    }
}
