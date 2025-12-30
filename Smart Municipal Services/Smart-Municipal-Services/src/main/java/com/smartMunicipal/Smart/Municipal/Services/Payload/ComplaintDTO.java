package com.smartMunicipal.Smart.Municipal.Services.Payload;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ComplaintDTO {
    private int id;
    private int userId;
    private String userName;
    private String userEmail;
    private String complaintType;
    private String complaintTitle;
    private String message;
    private LocalDateTime generatedAt;
    private String complaintStatus;
    private String imageUrl;
}
