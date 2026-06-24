package com.example.ecommerce.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.ecommerce.repository.BlacklistedTokenRepository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class BlacklistCleanupService {
    private final BlacklistedTokenRepository blacklistRepo;

    public BlacklistCleanupService(BlacklistedTokenRepository blacklistRepo) {
        this.blacklistRepo = blacklistRepo;
    }

    @Scheduled(cron = "0 0 * * * *") // every hour
    @Transactional
    public void cleanupBlacklist() {
        blacklistRepo.deleteByExpiryBefore(LocalDateTime.now());
    }

    @Transactional
    public long cleanupNow() {
        return blacklistRepo.deleteByExpiryBefore(LocalDateTime.now());
    }

    @Transactional
    public void forceCleanup() {
        blacklistRepo.deleteAll();
    }

    public boolean isBlacklisted(String token) {
        return blacklistRepo.existsByToken(token);
    }
}
