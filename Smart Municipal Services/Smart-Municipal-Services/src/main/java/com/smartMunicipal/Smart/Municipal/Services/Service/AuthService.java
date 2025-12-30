package com.smartMunicipal.Smart.Municipal.Services.Service;

import com.smartMunicipal.Smart.Municipal.Services.Payload.AuthResponse;
import com.smartMunicipal.Smart.Municipal.Services.Payload.LoginRequest;
import com.smartMunicipal.Smart.Municipal.Services.Payload.RegisterRequest;
import com.smartMunicipal.Smart.Municipal.Services.Payload.VerifyOtpRequest;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {
    String register(RegisterRequest request);
    String verifyOtp(VerifyOtpRequest request);
    AuthResponse login(LoginRequest request);
    String resendOtp(String email);
}

