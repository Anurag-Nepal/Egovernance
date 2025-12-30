package com.smartMunicipal.Smart.Municipal.Services.Controller;

import com.smartMunicipal.Smart.Municipal.Services.Payload.AddComplaintRequest;
import com.smartMunicipal.Smart.Municipal.Services.Payload.ApiResponse;
import com.smartMunicipal.Smart.Municipal.Services.Payload.ComplaintDTO;
import com.smartMunicipal.Smart.Municipal.Services.Service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    /**
     * Endpoint to raise a new complaint with an image
     * POST /api/complaints/raise
     */
    @PostMapping("/raise")
    public ResponseEntity<ApiResponse<String>> raiseComplaint(
            @RequestPart("complaint") AddComplaintRequest request,
            @RequestPart("image") MultipartFile image) {
        String response = complaintService.raiseComplaint(request, image);
        return ResponseEntity.ok(ApiResponse.success("Complaint raised successfully", response));
    }

    /**
     * Endpoint to get complaints with optional filters
     * GET /api/complaints?status=GENERATED&type=ROAD&date=2025-12-30
     */
    @GetMapping
    public ResponseEntity<ApiResponse<ComplaintDTO>> getComplaints(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String date) {
        ComplaintDTO complaint = complaintService.getComplaints(status, type, date);
        if (complaint == null) {
            return ResponseEntity.ok(ApiResponse.error(404, "No complaints found matching the criteria"));
        }
        return ResponseEntity.ok(ApiResponse.success("Complaint retrieved successfully", complaint));
    }

    /**
     * ADMIN ENDPOINT: Update complaint status to COMPLETED
     * PUT /api/complaints/{complaintId}/complete
     */
    @PutMapping("/{complaintId}/complete")
    public ResponseEntity<ApiResponse<Void>> updateComplaint(@PathVariable String complaintId) {
        String message = complaintService.updateComplaint(complaintId);
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    /**
     * ADMIN ENDPOINT: Delete a complaint
     * DELETE /api/complaints/{complaintId}
     */
    @DeleteMapping("/{complaintId}")
    public ResponseEntity<ApiResponse<Void>> deleteComplaint(@PathVariable String complaintId) {
        String message = complaintService.deleteComplaint(complaintId);
        return ResponseEntity.ok(ApiResponse.success(message));
    }
}

