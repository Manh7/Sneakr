package com.ducmanh.identityservice.mapper;

import com.ducmanh.identityservice.dto.request.PermissionRequest;
import com.ducmanh.identityservice.dto.response.PermissionResponse;
import com.ducmanh.identityservice.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
