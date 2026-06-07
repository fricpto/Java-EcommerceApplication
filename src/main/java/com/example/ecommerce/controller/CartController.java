package com.example.ecommerce.controller;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.dto.AddItemRequest;

@RestController
@RequestMapping("/api/user/cart")
public class CartController {

        @Autowired
        private CartService cartService;

        @Autowired
        private UserRepository userRepository;

        @GetMapping
        public ResponseEntity<Cart> getCart(@AuthenticationPrincipal UserDetails userDetails) {
                User user = userRepository.findByEmail(userDetails.getUsername())
                                .orElseThrow(() -> new RuntimeException("User not found"));
                return ResponseEntity.ok(cartService.getCartByUser(user));
        }

        @PostMapping("/add")
        public ResponseEntity<Cart> addItem(@AuthenticationPrincipal UserDetails userDetails,
                        @RequestBody AddItemRequest request) {
                User user = userRepository.findByEmail(userDetails.getUsername())
                                .orElseThrow(() -> new RuntimeException("User not found"));
                return ResponseEntity.ok(cartService.addItemToCart(user, request.getItemId(), request.getQuantity()));
        }

        @DeleteMapping("/remove")
        public ResponseEntity<Cart> removeItem(@AuthenticationPrincipal UserDetails userDetails,
                        @RequestParam Long itemId) {
                User user = userRepository.findByEmail(userDetails.getUsername())
                                .orElseThrow(() -> new RuntimeException("User not found"));
                return ResponseEntity.ok(cartService.removeItemFromCart(user, itemId));
        }

        @DeleteMapping("/clear")
        public ResponseEntity<Cart> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
                User user = userRepository.findByEmail(userDetails.getUsername())
                                .orElseThrow(() -> new RuntimeException("User not found"));
                return ResponseEntity.ok(cartService.clearCart(user));
        }

        @PutMapping("/items/{itemId}")
        public ResponseEntity<Cart> updateItemQuantity(@AuthenticationPrincipal UserDetails userDetails,
                        @PathVariable Long itemId,
                        @RequestParam int quantity) {
                User user = userRepository.findByEmail(userDetails.getUsername())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
                return ResponseEntity.ok(cartService.updateItemQuantity(user, itemId, quantity));
        }

}