package com.example.ecommerce.service;

import com.example.ecommerce.model.CreditCard;
import com.example.ecommerce.model.User;
import com.example.ecommerce.model.Wallet;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.repository.CreditCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CheckUserAndCardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    public void checkUserAndCard(String email, String cardNumber) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        Optional<CreditCard> creditCardOpt = creditCardRepository.findByNumber(cardNumber);

        creditCardOpt.ifPresent(card -> {
            Wallet wallet = card.getWallet();
            if (wallet != null && wallet.getUser() != null) {
                User owner = wallet.getUser();
                System.out.println("Card belongs to user: " + owner.getEmail());
            } else {
                System.out.println("Card is not linked to any user!");
            }
        });

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("Found user: " + user.getFullName());
        } else {
            System.out.println("User not found!");
        }
    }
}