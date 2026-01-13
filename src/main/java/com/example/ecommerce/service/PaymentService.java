package com.example.ecommerce.service;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private OrderRepository orderRepository;

    // Simulate processing a payment
    public Order processPayment(Order order) {
        // pretend payment succeeded
        order.setStatus(Order.Status.PAID);
        return orderRepository.save(order); // persist change
    }

    // Return the current payment status based on the order's status
    public String getPaymentStatus(Order order) {
        return order.getStatus().name(); // "PENDING", "PAID", "CANCELLED"
    }
}