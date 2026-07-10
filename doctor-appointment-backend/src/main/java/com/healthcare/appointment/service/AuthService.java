package com.healthcare.appointment.service;

import com.healthcare.appointment.dto.request.LoginRequest;
import com.healthcare.appointment.dto.request.RegisterRequest;
import com.healthcare.appointment.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    LoginResponse refreshToken(String refreshTokenStr);

    void logout(Long userId);
}
