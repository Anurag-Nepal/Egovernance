package com.smartMunicipal.Smart.Municipal.Services.Service;

import com.smartMunicipal.Smart.Municipal.Services.Payload.DocumentRequest;
import org.springframework.stereotype.Service;

@Service
public interface DocumentService {


    //user method for requesting the documents
    String requestForDocument(DocumentRequest request);

    //open endpoint to check the authenticity of the document via some id or unique identity
    Boolean checkDocumentAuthenticity(Integer documentId, String hash);


    //admin method to verify the authenticity of the application and verify the document.
    String verifyApplicationAndIssueDocument(Integer applicationId);

}
