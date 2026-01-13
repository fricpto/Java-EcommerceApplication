package com.example.ecommerce.repository;

import com.example.ecommerce.model.Payment;
import com.example.ecommerce.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Find payment by order
    Optional<Payment> findByOrder(Order order);

    // Find payments by status
    List<Payment> findByStatus(Payment.Status status);

    // Find payments by method
    List<Payment> findByMethod(Payment.Method method);

    // Find payment by order ID
    Optional<Payment> findByOrderId(Long orderId);
}