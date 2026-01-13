package com.example.ecommerce.controller;

import com.example.ecommerce.model.CreditCard;
import com.example.ecommerce.model.User;
import com.example.ecommerce.service.WalletService;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user/cards")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserRepository userRepository;

    public static class AddCardRequest {
        public String number;
    }

    @PostMapping
    public ResponseEntity<CreditCard> addCard(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AddCardRequest req) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        CreditCard saved = walletService.addCardToWallet(user, req.number);
        // mask number before returning (simple example: show last 4)
        String num = saved.getNumber();
        String masked = num == null ? null
                : (num.length() > 4 ? "**** **** **** " + num.substring(num.length() - 4) : num);
        saved.setNumber(masked);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<CreditCard>> listCards(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CreditCard> cards = walletService.listCards(user);
        // mask numbers before returning
        cards.forEach(c -> {
            String num = c.getNumber();
            String masked = num == null ? null
                    : (num.length() > 4 ? "**** **** **** " + num.substring(num.length() - 4) : num);
            c.setNumber(masked);
        });
        return ResponseEntity.ok(cards);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<?> removeCard(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long cardId) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        walletService.removeCard(user, cardId);
        return ResponseEntity.ok(Map.of("message", "Card removed"));
    }
}
