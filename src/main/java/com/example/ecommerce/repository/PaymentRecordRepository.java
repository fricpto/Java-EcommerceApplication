package com.example.ecommerce.repository;

import com.example.ecommerce.model.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {
    List<PaymentRecord> findByWalletId(Long walletId);
}
