package com.ducmanh.identityservice.controller;

import com.ducmanh.identityservice.dto.ApiResponse;
import com.ducmanh.identityservice.dto.request.PermissionRequest;
import com.ducmanh.identityservice.dto.response.PermissionResponse;
import com.ducmanh.identityservice.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @PostMapping
    public ApiResponse<PermissionResponse> create(@RequestBody PermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.create(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<PermissionResponse>> getAll() {
        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionService.getAll())
                .build();
    }

    @DeleteMapping("/{permissionId}")
    public ApiResponse<String> delete(@PathVariable String permissionId) {
        permissionService.delete(permissionId);
        return ApiResponse.<String>builder()
                .result("Permission has been deleted")
                .build();
    }
}
