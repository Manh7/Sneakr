package com.ducmanh.identityservice.service;

import com.ducmanh.identityservice.dto.request.IntrospectRequest;
import com.ducmanh.identityservice.dto.request.LoginRequest;
import com.ducmanh.identityservice.dto.request.LogoutRequest;
import com.ducmanh.identityservice.dto.request.RefreshRequest;
import com.ducmanh.identityservice.dto.response.AuthenticationResponse;
import com.ducmanh.identityservice.dto.response.IntrospectResponse;
import com.nimbusds.jose.JOSEException;
import org.springframework.stereotype.Service;

import java.text.ParseException;

public interface AuthenticationService {
    AuthenticationResponse login(LoginRequest request);
    IntrospectResponse introspect(IntrospectRequest request) throws ParseException, JOSEException;
    void logout(LogoutRequest request) throws ParseException, JOSEException;
    AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException;
}
