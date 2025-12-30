package com.smartMunicipal.Smart.Municipal.Services.Service;

import org.springframework.stereotype.Service;

@Service
public interface OtpService {
    String generateOtp();
    void sendOtpEmail(String email, String otp, String purpose);
    boolean validateOtp(String email, String otp);
}

