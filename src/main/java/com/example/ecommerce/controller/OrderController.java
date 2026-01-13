package com.example.ecommerce.controller;

import com.example.ecommerce.dto.PayRequest;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.Payment;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.ecommerce.repository.OrderRepository;
import java.util.List;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping({ "/api/user/orders", "/api/admin/orders" })
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/place")
    public ResponseEntity<Order> placeOrder(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        log.info("placeOrder called for principal: {}", userDetails == null ? "null" : userDetails.getUsername());
        return ResponseEntity.ok(orderService.placeOrder(user));
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrders(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return ResponseEntity.ok(orderService.getOrdersByUser(user));
    }

    @DeleteMapping("/cancel/{orderId}")
    public ResponseEntity<Order> cancelOrder(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        Order cancelled = orderService.cancelOrder(user, orderId);
        return ResponseEntity.ok(cancelled);
    }

    @PutMapping("/{orderId}/pay")
    public ResponseEntity<Order> markOrderAsPaid(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId,
            @RequestBody(required = false) PayRequest req) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        log.info("Pay request principalUsername={}, principalUserIdCandidate={}, orderId={}, orderOwnerId={}",
                userDetails == null ? "null" : userDetails.getUsername(),
                user == null ? "null" : user.getId(),
                orderId,
                order.getUser() == null ? "null" : order.getUser().getId());

        if (!order.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }

        // --- parse request body first ---
        Payment.Method requestedMethod = null;
        Long requestedCardId = null;

        if (req != null) {
            requestedCardId = req.cardId;
            if (req.method != null && !req.method.isBlank()) {
                try {
                    requestedMethod = Payment.Method.valueOf(req.method.trim().toUpperCase());
                } catch (IllegalArgumentException ex) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid payment method");
                }
            }
        }

        log.info("pay request parsed: orderId={}, method={}, cardId={}", orderId, requestedMethod, requestedCardId);

        // Now call the service with the parsed values
        Order updated = orderService.markOrderAsPaid(user, orderId, requestedMethod, requestedCardId);

        // break cycles / hide sensitive fields
        if (updated.getItems() != null) {
            updated.getItems().forEach(oi -> oi.setOrder(null));
        }
        if (updated.getUser() != null) {
            updated.getUser().setPasswordHash(null);
        }

        return ResponseEntity.ok(updated);
    }

    // in OrderController
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Order order = orderRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        // break cycles and remove sensitive fields
        if (order.getUser() != null) {
            order.getUser().setPasswordHash(null);
            order.getUser().setCart(null);
            order.getUser().setOrders(null);
        }
        if (order.getItems() != null) {
            order.getItems().forEach(oi -> oi.setOrder(null));
        }

        return ResponseEntity.ok(order);
    }

}