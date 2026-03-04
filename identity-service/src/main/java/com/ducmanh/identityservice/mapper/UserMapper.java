package com.ducmanh.identityservice.mapper;

import com.ducmanh.identityservice.dto.request.UserCreationRequest;
import com.ducmanh.identityservice.dto.request.UserUpdateRequest;
import com.ducmanh.identityservice.dto.response.UserResponse;
import com.ducmanh.identityservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
