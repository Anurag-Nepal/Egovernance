package com.smartMunicipal.Smart.Municipal.Services.Service;

import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    void sendEmailWithAttachment(String toEmail, String subject, String body, byte[] attachmentData, String filename);
}
