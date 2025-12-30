package com.smartMunicipal.Smart.Municipal.Services.Entity;

import com.smartMunicipal.Smart.Municipal.Services.Enums.ComplaintStatus;
import com.smartMunicipal.Smart.Municipal.Services.Enums.ComplaintType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(50)")
    private ComplaintType complaintType;
    
    private String complaintTitle;
    
    @Lob
    private String message;
    
    private LocalDateTime generatedAt;
    
    private String complaintImage;
    
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(50)")
    private ComplaintStatus complaintStatus;
}
