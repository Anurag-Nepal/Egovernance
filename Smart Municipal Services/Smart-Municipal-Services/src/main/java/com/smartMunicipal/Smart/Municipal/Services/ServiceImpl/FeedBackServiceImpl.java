package com.smartMunicipal.Smart.Municipal.Services.ServiceImpl;

import com.smartMunicipal.Smart.Municipal.Services.Entity.Complaint;
import com.smartMunicipal.Smart.Municipal.Services.Entity.Feedback;
import com.smartMunicipal.Smart.Municipal.Services.Entity.User;
import com.smartMunicipal.Smart.Municipal.Services.Payload.CreateFeedbackRequest;
import com.smartMunicipal.Smart.Municipal.Services.Payload.RatingDTO;
import com.smartMunicipal.Smart.Municipal.Services.Repository.ComplaintRepository;
import com.smartMunicipal.Smart.Municipal.Services.Repository.FeedbackRepository;
import com.smartMunicipal.Smart.Municipal.Services.Repository.UserRepository;
import com.smartMunicipal.Smart.Municipal.Services.Service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FeedBackServiceImpl implements FeedbackService {
    
    @Autowired
    private FeedbackRepository feedbackRepository;
    
    @Autowired
    private ComplaintRepository complaintRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    
    public String getCurrentUsername() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userName == null) {
            throw new RuntimeException("User Not Authenticated");
        }
        return userName;
        
    }
    
    @Override
    public String createFeedback(CreateFeedbackRequest request) {
        // Validate request
        try {
            if (request == null) {
                throw new IllegalArgumentException("Feedback request cannot be null");
            }
            
            if (request.getRating() < 1 || request.getRating() > 5) {
                throw new IllegalArgumentException("Rating must be between 1 and 5");
            }
            
            // Find the complaint
            Complaint complaint = complaintRepository.findById(request.getComplaintId())
                    .orElseThrow(() -> new RuntimeException("Complaint not found with id: " + request.getComplaintId()));
            
            String userName = getCurrentUsername();
            
            if (!complaint.getUser().getUsername().equals(userName)) {
                throw new RuntimeException("This user is prohibited from giving feedback on this complaint");
            }
            // Create feedback entity
            Feedback feedback = new Feedback();
            feedback.setComplaint(complaint);
            feedback.setRating(request.getRating());
            feedback.setDescriptionOfFeedback(request.getDescription());
            
            // Save feedback
            feedbackRepository.save(feedback);
            
            return "Feedback created successfully for complaint ID: " + request.getComplaintId();
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            // Catch duplicate entry exception
            return "Feedback already exists for complaint ID: " + request.getComplaintId();
        }
    }

    @Override
    public RatingDTO calculateOverallRatingOfGovernment() {
        // Get all feedbacks
        List<Feedback> feedbacks = feedbackRepository.findAll();
        
        if (feedbacks.isEmpty()) {
            return new RatingDTO();
        }
        
        // Calculate average rating
        double averageRating = feedbacks.stream()
                .mapToLong(Feedback::getRating)
                .average()
                .orElse(0.0);
        
        RatingDTO ratingDTO = new RatingDTO();
        ratingDTO.setRating(Math.round(averageRating));
        
        return ratingDTO;
    }
}
