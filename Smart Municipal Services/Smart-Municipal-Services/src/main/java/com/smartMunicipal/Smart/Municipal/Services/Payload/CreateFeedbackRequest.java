package com.smartMunicipal.Smart.Municipal.Services.Payload;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateFeedbackRequest {

        private int complaintId;
        private String description;
        private long rating;

}
