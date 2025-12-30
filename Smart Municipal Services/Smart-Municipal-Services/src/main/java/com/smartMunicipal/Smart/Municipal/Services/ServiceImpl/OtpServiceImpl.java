package com.smartMunicipal.Smart.Municipal.Services.ServiceImpl;

import com.smartMunicipal.Smart.Municipal.Services.Entity.OtpVerification;
import com.smartMunicipal.Smart.Municipal.Services.Repository.OtpVerificationRepository;
import com.smartMunicipal.Smart.Municipal.Services.Service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpServiceImpl implements OtpService {

    @Autowired
    private OtpVerificationRepository otpRepository;

    @Autowired
    private JavaMailSender mailSender;

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 10;

    @Override
    public String generateOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    @Override
    public void sendOtpEmail(String email, String otp, String purpose) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Smart Municipal Services - OTP Verification");
            message.setText(buildEmailContent(otp, purpose));
            message.setFrom("pennywisenepal@gmail.com");
            
            mailSender.send(message);

            // Save OTP to database
            OtpVerification otpVerification = new OtpVerification();
            otpVerification.setEmail(email);
            otpVerification.setOtp(otp);
            otpVerification.setPurpose(purpose);
            otpVerification.setCreatedAt(LocalDateTime.now());
            otpVerification.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES));
            otpVerification.setVerified(false);
            
            otpRepository.save(otpVerification);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP email: " + e.getMessage());
        }
    }

    @Override
    public boolean validateOtp(String email, String otp) {
        Optional<OtpVerification> otpOpt = otpRepository.findByEmailAndOtpAndVerifiedFalse(email, otp);
        
        if (otpOpt.isEmpty()) {
            return false;
        }

        OtpVerification otpVerification = otpOpt.get();
        
        // Check if OTP is expired
        if (LocalDateTime.now().isAfter(otpVerification.getExpiresAt())) {
            return false;
        }

        // Mark as verified
        otpVerification.setVerified(true);
        otpVerification.setVerifiedAt(LocalDateTime.now());
        otpRepository.save(otpVerification);
        
        return true;
    }

    private String buildEmailContent(String otp, String purpose) {
        return String.format("""
            Dear User,
            
            Your OTP for %s is: %s
            
            This OTP is valid for %d minutes.
            
            If you did not request this, please ignore this email.
            
            Best regards,
            Smart Municipal Services Team
            """, purpose, otp, OTP_EXPIRY_MINUTES);
    }
}
