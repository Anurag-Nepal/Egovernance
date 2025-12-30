package com.smartMunicipal.Smart.Municipal.Services.Controller;

import com.smartMunicipal.Smart.Municipal.Services.Payload.ApiResponse;
import com.smartMunicipal.Smart.Municipal.Services.Payload.DocumentDTO;
import com.smartMunicipal.Smart.Municipal.Services.Payload.DocumentRequest;
import com.smartMunicipal.Smart.Municipal.Services.Service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    /**
     * Endpoint for users to request a new document
     * POST /api/documents/request
     */
    @PostMapping("/request")
    public ResponseEntity<ApiResponse<Void>> requestDocument(@RequestBody DocumentRequest request) {
        String message = documentService.requestForDocument(request);
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    /**
     * PUBLIC ENDPOINT: Verify document authenticity by scanning QR code
     * GET /api/documents/verify/{docId}/{hash}
     * QR Code URL format: http://yourdomain.com/api/documents/verify/123/ABC123XYZ
     * Path variables are better for QR scanning - no URL encoding issues
     */
    @GetMapping("/verify/{docId}/{hash}")
    public ResponseEntity<ApiResponse<VerificationResponse>> verifyDocument(
            @PathVariable("docId") Integer documentId,
            @PathVariable("hash") String hash) {
        
        Boolean isAuthentic = documentService.checkDocumentAuthenticity(documentId, hash);
        
        VerificationResponse verificationData = new VerificationResponse(
                isAuthentic,
                isAuthentic ? "Document is authentic and verified." : "Document verification failed. This document may be fraudulent.",
                documentId
        );
        
        String message = isAuthentic ? "Document verified successfully" : "Document verification failed";
        return ResponseEntity.ok(ApiResponse.success(message, verificationData));
    }

    /**
     * ADMIN ENDPOINT: Verify application and issue document
     * POST /api/documents/issue/{applicationId}
     */
    @PostMapping("/issue/{applicationId}")
    public ResponseEntity<ApiResponse<Void>> issueDocument(@PathVariable Integer applicationId) {
        String message = documentService.verifyApplicationAndIssueDocument(applicationId);
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    /**
     * ADMIN ENDPOINT: Get all documents
     * GET /api/documents
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<DocumentDTO>>> getAllDocuments() {
        List<DocumentDTO> documents = documentService.findAll();
        return ResponseEntity.ok(ApiResponse.success("Documents retrieved successfully", documents));
    }

    /**
     * Response DTO for document verification
     */
    public record VerificationResponse(
            boolean isAuthentic,
            String message,
            Integer documentId
    ) {
    
    }
}

