package com.smartMunicipal.Smart.Municipal.Services.Payload;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AddComplaintRequest {

    private String complaintType;

    private String complaintTitle;

    private String message;



}
