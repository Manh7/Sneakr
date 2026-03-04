package com.ducmanh.identityservice.service;

import com.ducmanh.identityservice.dto.request.PermissionRequest;
import com.ducmanh.identityservice.dto.response.PermissionResponse;
import org.springframework.stereotype.Service;

import java.util.List;

public interface PermissionService {
    PermissionResponse create(PermissionRequest request);
    List<PermissionResponse> getAll();
    void delete(String permission);
}
