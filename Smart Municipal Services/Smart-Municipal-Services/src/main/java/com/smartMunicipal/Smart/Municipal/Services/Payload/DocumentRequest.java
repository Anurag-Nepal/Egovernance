package com.smartMunicipal.Smart.Municipal.Services.Payload;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentRequest {
 private String title;
 private String issuedByUserId;
 private String documentCategory;
private String documentStatus;

}
