package com.smartMunicipal.Smart.Municipal.Services.Service;

import com.smartMunicipal.Smart.Municipal.Services.Payload.AddComplaintRequest;
import com.smartMunicipal.Smart.Municipal.Services.Payload.ComplaintDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface ComplaintService {

    String raiseComplaint(AddComplaintRequest request, MultipartFile image);


    ComplaintDTO getComplaints(String complaintStatus, String complaintType,String complaintDate);

    String updateComplaint(String complaintId);

    String deleteComplaint(String complaintId);


}
