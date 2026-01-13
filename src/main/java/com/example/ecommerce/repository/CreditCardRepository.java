package com.example.ecommerce.repository;

import com.example.ecommerce.model.CreditCard;
import com.example.ecommerce.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {
    Optional<CreditCard> findByNumber(String number);

    List<CreditCard> findByWallet(Wallet wallet);

    List<CreditCard> findByWalletId(Long walletId);
}