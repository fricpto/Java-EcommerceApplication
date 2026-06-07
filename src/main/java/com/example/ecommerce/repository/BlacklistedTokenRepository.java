package com.example.ecommerce.repository;

import com.example.ecommerce.model.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {

    // Check if token exists
    boolean existsByToken(String token);

    // Cleanup expired tokens
    Long deleteByExpiryBefore(LocalDateTime date);

    // void deleteAll();

}
