package com.example.ecommerce.controller;

import com.example.ecommerce.dto.RegisterRequest;
import com.example.ecommerce.model.User;
import com.example.ecommerce.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    // ---------------- REGISTER ----------------
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest req) {
        log.info("Register request: email={}, passwordPresent={}", req.getEmail(), req.getPassword() != null);
        User created = authService.registerUser(req);
        return ResponseEntity.ok(created);
    }

    // ---------------- LOGIN ----------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RegisterRequest loginRequest) {
        try {
            String token = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(java.util.Map.of("jwt", token));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    // ---------------- LOGOUT ----------------
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(name = "Authorization") String authHeader) {
        log.info("Logout request: {}", authHeader);

        // Extract token if header starts with "Bearer "
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        // Call service to handle logout (blacklist or just acknowledge)
        authService.logout(token);

        return ResponseEntity.ok(java.util.Map.of("message", "Logged out successfully"));
    }

}
