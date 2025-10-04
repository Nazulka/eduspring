package com.lms.eduspring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue
    private UUID id; // primary key, auto-generated

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private String role; // e.g., STUDENT, TEACHER, ADMIN

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // --- Constructors ---
    public User() {
        // Default constructor required by JPA
    }

    public User(String username, String passwordHash, String firstName,
                String lastName, String email, String role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Optional: Convenience method to update timestamps
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
