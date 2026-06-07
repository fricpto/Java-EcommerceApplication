package com.example.ecommerce.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blacklisted_tokens")
public class BlacklistedToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiry;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // getters and setters

    public Long getId() {
        return id;
    }

    public Long setId(Long id) {
        this.id = id;
        return id;
    }

    public String getToken() {
        return token;
    }

    public String setToken(String token) {
        this.token = token;
        return token;
    }

    public LocalDateTime getExpiry() {
        return expiry;
    }

    public LocalDateTime setExpiry(LocalDateTime expiry) {
        this.expiry = expiry;
        return expiry;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return createdAt;
    }
}
