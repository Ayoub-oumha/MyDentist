package com.example.mydentist2.service;

import com.example.mydentist2.dto.auth.AuthResponse;
import com.example.mydentist2.dto.auth.LoginRequest;
import com.example.mydentist2.dto.auth.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
