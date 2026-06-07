package com.example.ecommerce.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;
import com.example.ecommerce.repository.BlacklistedTokenRepository;
import com.example.ecommerce.service.BlacklistCleanupService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/admin/blacklist")
public class BlacklistAdminController {

    private final BlacklistCleanupService cleanupService;
    /* private final BlacklistedTokenRepository blacklistRepo; */
    private static final Logger log = LoggerFactory.getLogger(BlacklistAdminController.class);

    public BlacklistAdminController(
            /* BlacklistedTokenRepository blacklistRepo, */BlacklistCleanupService cleanupService) {
        /* this.blacklistRepo = blacklistRepo; */
        this.cleanupService = cleanupService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/cleanup")
    public ResponseEntity<?> cleanupNow() {
        log.info("Running cleanup at {}", LocalDateTime.now());
        try {
            Long deletedCount = cleanupService.cleanupNow();
            return ResponseEntity.ok(Map.of("message", "Blacklist cleanup executed", "deletedCount", deletedCount));
        } catch (Exception e) {
            e.printStackTrace(); // or log.error(...)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cleanup failed", e);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/force-cleanup")
    public ResponseEntity<?> forceCleanup() {
        cleanupService.forceCleanup();
        return ResponseEntity.ok(Map.of("message", "Force cleanup executed"));
    }

}
