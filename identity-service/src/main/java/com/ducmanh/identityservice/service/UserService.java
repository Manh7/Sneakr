package com.ducmanh.identityservice.service;

import com.ducmanh.identityservice.dto.request.UserCreationRequest;
import com.ducmanh.identityservice.dto.request.UserUpdateRequest;
import com.ducmanh.identityservice.dto.response.UserResponse;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserCreationRequest request);
    List<UserResponse> getUsers();
    UserResponse getMyInfo();
    UserResponse getUser(String id);
    UserResponse updateUser(String id, UserUpdateRequest request);
    void deleteUser(String id);
}
