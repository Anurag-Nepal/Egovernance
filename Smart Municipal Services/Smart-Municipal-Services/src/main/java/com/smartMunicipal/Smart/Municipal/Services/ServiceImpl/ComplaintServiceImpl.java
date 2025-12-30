package com.smartMunicipal.Smart.Municipal.Services.ServiceImpl;

import com.smartMunicipal.Smart.Municipal.Services.Entity.Complaint;
import com.smartMunicipal.Smart.Municipal.Services.Entity.User;
import com.smartMunicipal.Smart.Municipal.Services.Enums.ComplaintStatus;
import com.smartMunicipal.Smart.Municipal.Services.Enums.ComplaintType;
import com.smartMunicipal.Smart.Municipal.Services.Payload.AddComplaintRequest;
import com.smartMunicipal.Smart.Municipal.Services.Payload.ComplaintDTO;
import com.smartMunicipal.Smart.Municipal.Services.Repository.ComplaintRepository;
import com.smartMunicipal.Smart.Municipal.Services.Repository.UserRepository;
import com.smartMunicipal.Smart.Municipal.Services.Service.ComplaintService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements ComplaintService {


    private final ComplaintRepository complaintRepository;
    private final FileStorageServiceImpl storageService;
    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;



    @Override
    public String raiseComplaint(AddComplaintRequest request, MultipartFile image) {
        // Get logged-in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        ComplaintType type;
        try {
            type = ComplaintType.valueOf(request.getComplaintType().toUpperCase());
        } catch (IllegalArgumentException e) {
            return "Invalid complaint type: " + request.getComplaintType();
        }

        Complaint complaint = new Complaint();
        complaint.setUser(user); // Set the user who raised the complaint
        complaint.setComplaintType(type);
        complaint.setComplaintTitle(request.getComplaintTitle());
        complaint.setMessage(request.getMessage());
        complaint.setGeneratedAt(LocalDateTime.now());
        complaint.setComplaintStatus(ComplaintStatus.GENERATED);
        String url = storageService.saveImage(image);
        complaint.setComplaintImage(url);
        complaintRepository.save(complaint);
        return "Complaint raised successfully with ID: " + complaint.getId();
    }

    @Override
    public ComplaintDTO getComplaints(String complaintStatus, String complaintType, String complaintDate) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Complaint> query = cb.createQuery(Complaint.class);
        Root<Complaint> root = query.from(Complaint.class);

        Predicate predicate = cb.conjunction();

        if (complaintStatus != null && !complaintStatus.isEmpty()) {
            try {
                ComplaintStatus statusEnum = ComplaintStatus.valueOf(complaintStatus.toUpperCase());
                predicate = cb.and(predicate, cb.equal(root.get("complaintStatus"), statusEnum));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid complaintStatus: " + complaintStatus);
            }
        }

        if (complaintType != null && !complaintType.isEmpty()) {
            try {
                ComplaintType typeEnum = ComplaintType.valueOf(complaintType.toUpperCase());
                predicate = cb.and(predicate, cb.equal(root.get("complaintType"), typeEnum));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid complaintType: " + complaintType);
            }
        }

        if (complaintDate != null && !complaintDate.isEmpty()) {
            LocalDate date = LocalDate.parse(complaintDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();
            predicate = cb.and(predicate, cb.between(root.get("generatedAt"), start, end));
        }

        query.where(predicate);
        query.orderBy(cb.desc(root.get("generatedAt")));

        Complaint complaint = entityManager.createQuery(query)
                .setMaxResults(1)
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (complaint == null) return null;

        return ComplaintDTO.builder()
                .id(complaint.getId())
                .userId(complaint.getUser() != null ? complaint.getUser().getId() : 0)
                .userName(complaint.getUser() != null ? complaint.getUser().getFullName() : "Unknown")
                .userEmail(complaint.getUser() != null ? complaint.getUser().getEmail() : "Unknown")
                .complaintType(String.valueOf(complaint.getComplaintType()))
                .complaintTitle(complaint.getComplaintTitle())
                .message(complaint.getMessage())
                .generatedAt(complaint.getGeneratedAt())
                .complaintStatus(String.valueOf(complaint.getComplaintStatus()))
                .imageUrl(complaint.getComplaintImage())
                .build();
    }

    @Override
    public String updateComplaint(String complaintId) {
        Optional<Complaint> complaintOpt = complaintRepository.findById(Integer.parseInt(complaintId));
        if (complaintOpt.isPresent()) {
            Complaint complaint = complaintOpt.get();
            complaint.setComplaintStatus(ComplaintStatus.COMPLETED);
            complaintRepository.save(complaint);
            return "Complaint with ID " + complaintId + " updated to RESOLVED.";
        } else {
            return "Complaint with ID " + complaintId + " not found.";
        }
    }

    @Override
    public String deleteComplaint(String complaintId) {
        int id = Integer.parseInt(complaintId);
        if (complaintRepository.existsById(id)) {
            complaintRepository.deleteById(id);
            return "Complaint with ID " + complaintId + " deleted.";
        } else {
            return "Complaint with ID " + complaintId + " not found.";
        }

    }
}