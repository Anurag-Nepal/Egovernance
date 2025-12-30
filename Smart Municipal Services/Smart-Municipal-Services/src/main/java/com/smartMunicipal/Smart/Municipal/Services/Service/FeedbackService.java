package com.smartMunicipal.Smart.Municipal.Services.Service;

import com.smartMunicipal.Smart.Municipal.Services.Payload.CreateFeedbackRequest;
import com.smartMunicipal.Smart.Municipal.Services.Payload.RatingDTO;
import org.springframework.stereotype.Service;

@Service
public interface FeedbackService {
    String createFeedback(CreateFeedbackRequest request);
    RatingDTO calculateOverallRatingOfGovernment();
}
