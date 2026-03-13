package com.ducmanh.cartservice.httpClient;

import com.ducmanh.cartservice.dto.ApiResponse;
import com.ducmanh.cartservice.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "identity-service", url = "http://localhost:8080/identity")
public interface IdentityClient {
    @GetMapping("/users/myInfo")
    ApiResponse<UserResponse> getMyInfo(@RequestHeader("Authorization") String token);
}
