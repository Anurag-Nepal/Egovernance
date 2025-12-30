package com.smartMunicipal.Smart.Municipal.Services.Entity;

import com.smartMunicipal.Smart.Municipal.Services.Enums.DocumentCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_to_id")
    private User issuedTo;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(50)")
    private DocumentCategory category;

    private String documentHash;

    private LocalDateTime issuedAt;

}
