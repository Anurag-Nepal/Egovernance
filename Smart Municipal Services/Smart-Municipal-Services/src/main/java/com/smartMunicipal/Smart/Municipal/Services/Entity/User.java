package com.smartMunicipal.Smart.Municipal.Services.Entity;

import com.smartMunicipal.Smart.Municipal.Services.Enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String fullName;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(50)")
    private Role role;

    private boolean enabled = false; // Account is enabled after OTP verification

    private boolean emailVerified = false;

    private LocalDateTime createdAt;

    private LocalDateTime lastLogin;

}
