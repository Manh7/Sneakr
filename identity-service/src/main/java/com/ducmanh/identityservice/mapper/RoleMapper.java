package com.ducmanh.identityservice.mapper;

import com.ducmanh.identityservice.dto.request.RoleRequest;
import com.ducmanh.identityservice.dto.response.RoleResponse;
import com.ducmanh.identityservice.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
