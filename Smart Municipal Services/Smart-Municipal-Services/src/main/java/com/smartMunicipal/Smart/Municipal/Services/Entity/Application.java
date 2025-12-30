package com.smartMunicipal.Smart.Municipal.Services.Entity;

import com.smartMunicipal.Smart.Municipal.Services.Enums.ApplicationStatus;
import com.smartMunicipal.Smart.Municipal.Services.Enums.DocumentCategory;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String documentTitle;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(50)")
    private DocumentCategory category;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(50)")
    private ApplicationStatus status;

    private LocalDateTime submittedAt;

    private LocalDateTime processedAt;
}