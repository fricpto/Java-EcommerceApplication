package com.example.ecommerce.controller;

import com.example.ecommerce.dto.SimulatePaymentRequest;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.PaymentService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.example.ecommerce.dto.PaymentResponse;
import com.example.ecommerce.repository.WalletRepository;

@RestController
@RequestMapping("/api/user/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WalletRepository walletRepository;

    // Process payment for an order
    @PostMapping("/pay/{orderId}")
    public ResponseEntity<Order> payForOrder(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You cannot pay for someone else's order");
        }

        Order paidOrder = paymentService.processPayment(order);
        return ResponseEntity.ok(paidOrder);
    }

    // Check payment status
    @GetMapping("/status/{orderId}")
    public ResponseEntity<String> getPaymentStatus(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You cannot check someone else's order");
        }

        String status = paymentService.getPaymentStatus(order);
        return ResponseEntity.ok("Payment status for order " + orderId + ": " + status);
    }

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/simulate")
    public ResponseEntity<PaymentResponse> simulate(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid SimulatePaymentRequest req) {

        var user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // assume WalletRepository has findByUser(User user) or user.getWallet()
        var wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for user"));

        PaymentResponse resp = paymentService.simulatePayment(req, wallet.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

}