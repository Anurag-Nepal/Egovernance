package com.smartMunicipal.Smart.Municipal.Services.Payload;

import com.smartMunicipal.Smart.Municipal.Services.Entity.Document;
import com.smartMunicipal.Smart.Municipal.Services.Enums.DocumentCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentDTO {
    private Integer id;
    private String title;
    private DocumentCategory category;
    private String documentHash;
    private LocalDateTime issuedAt;
    private Integer issuedToUserId;
    private String issuedToUserName;
    private String issuedToEmail;

    public static DocumentDTO fromEntity(Document document) {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(document.getId());
        dto.setTitle(document.getTitle());
        dto.setCategory(document.getCategory());
        dto.setDocumentHash(document.getDocumentHash());
        dto.setIssuedAt(document.getIssuedAt());
        
        if (document.getIssuedTo() != null) {
            dto.setIssuedToUserId(document.getIssuedTo().getId());
            dto.setIssuedToUserName(document.getIssuedTo().getFullName());
            dto.setIssuedToEmail(document.getIssuedTo().getEmail());
        }
        
        return dto;
    }
}

