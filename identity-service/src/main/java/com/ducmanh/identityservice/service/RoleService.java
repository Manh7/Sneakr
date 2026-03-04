package com.ducmanh.identityservice.service;

import com.ducmanh.identityservice.dto.request.RoleRequest;
import com.ducmanh.identityservice.dto.response.RoleResponse;
import org.springframework.stereotype.Service;

import java.util.List;

public interface RoleService {
    RoleResponse create (RoleRequest request);
    List<RoleResponse> getAll();
    void delete (String role);
}
