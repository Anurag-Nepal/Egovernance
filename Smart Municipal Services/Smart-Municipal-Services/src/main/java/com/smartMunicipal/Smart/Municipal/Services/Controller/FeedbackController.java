package com.smartMunicipal.Smart.Municipal.Services.Controller;

import com.smartMunicipal.Smart.Municipal.Services.Payload.CreateFeedbackRequest;
import com.smartMunicipal.Smart.Municipal.Services.Payload.RatingDTO;
import com.smartMunicipal.Smart.Municipal.Services.Service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    /**
     * Endpoint to create feedback for a complaint
     * POST /api/feedback
     */
    @PostMapping
    public ResponseEntity<String> createFeedback(@RequestBody CreateFeedbackRequest request) {
        String response = feedbackService.createFeedback(request);
        return ResponseEntity.ok(response);
    }

    /**
     * PUBLIC ENDPOINT: Get overall rating statistics
     * GET /api/feedback/rating
     */
    @GetMapping("/rating")
    public ResponseEntity<RatingDTO> getOverallRating() {
        RatingDTO rating = feedbackService.calculateOverallRatingOfGovernment();
        return ResponseEntity.ok(rating);
    }
}

