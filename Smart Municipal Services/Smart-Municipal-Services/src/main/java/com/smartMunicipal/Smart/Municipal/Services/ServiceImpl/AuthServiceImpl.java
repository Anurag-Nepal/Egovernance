package com.smartMunicipal.Smart.Municipal.Services.ServiceImpl;

import com.smartMunicipal.Smart.Municipal.Services.Entity.User;
import com.smartMunicipal.Smart.Municipal.Services.Enums.Role;
import com.smartMunicipal.Smart.Municipal.Services.Exception.BadRequestException;
import com.smartMunicipal.Smart.Municipal.Services.Exception.ResourceNotFoundException;
import com.smartMunicipal.Smart.Municipal.Services.Payload.AuthResponse;
import com.smartMunicipal.Smart.Municipal.Services.Payload.LoginRequest;
import com.smartMunicipal.Smart.Municipal.Services.Payload.RegisterRequest;
import com.smartMunicipal.Smart.Municipal.Services.Payload.VerifyOtpRequest;
import com.smartMunicipal.Smart.Municipal.Services.Repository.UserRepository;
import com.smartMunicipal.Smart.Municipal.Services.Service.AuthService;
import com.smartMunicipal.Smart.Municipal.Services.Service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OtpService otpService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public String register(RegisterRequest request) {
        // Validate input
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new BadRequestException("Username is required");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new BadRequestException("Password must be at least 6 characters long");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        // Create new user
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER); // Default role
        user.setEnabled(false); // Will be enabled after OTP verification
        user.setEmailVerified(false);
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        // Generate and send OTP
        String otp = otpService.generateOtp();
        otpService.sendOtpEmail(request.getEmail(), otp, "REGISTRATION");

        return "Registration successful! Please check your email for OTP verification code.";
    }

    @Override
    @Transactional
    public String verifyOtp(VerifyOtpRequest request) {
        // Validate OTP
        boolean isValid = otpService.validateOtp(request.getEmail(), request.getOtp());
        
        if (!isValid) {
            throw new BadRequestException("Invalid or expired OTP");
        }

        // Find user and enable account
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));

        user.setEnabled(true);
        user.setEmailVerified(true);
        userRepository.save(user);

        return "Email verified successfully! You can now login.";
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            // Find user first to check if account is enabled
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            if (!user.isEnabled()) {
                throw new BadRequestException("Account not activated. Please verify your email first.");
            }

            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // Update last login
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            // Generate basic token (username:password in base64)
            String token = Base64.getEncoder().encodeToString(
                    (request.getUsername() + ":" + request.getPassword()).getBytes()
            );

            // Create response
            AuthResponse response = new AuthResponse();
            response.setToken(token);
            response.setUsername(user.getUsername());
            response.setEmail(user.getEmail());
            response.setRole(user.getRole().name());
            response.setMessage("Login successful");

            return response;

        } catch (AuthenticationException e) {
            throw new BadRequestException("Invalid username or password");
        }
    }

    @Override
    @Transactional
    public String resendOtp(String email) {
        // Check if user exists
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (user.isEmailVerified()) {
            throw new BadRequestException("Email already verified");
        }

        // Generate and send new OTP
        String otp = otpService.generateOtp();
        otpService.sendOtpEmail(email, otp, "REGISTRATION");

        return "OTP resent successfully! Please check your email.";
    }
}

