package com.example.ecommerce.service;

import com.example.ecommerce.model.CreditCard;
import com.example.ecommerce.model.User;
import com.example.ecommerce.model.Wallet;
import com.example.ecommerce.repository.CreditCardRepository;
import com.example.ecommerce.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.Collections;
import java.util.List;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    /**
     * Ensure wallet exists for user and return it.
     */
    public Wallet ensureWalletForUser(User user) {
        return walletRepository.findByUser(user)
                .orElseGet(() -> {
                    Wallet w = new Wallet();
                    w.setUser(user);
                    return walletRepository.save(w);
                });
    }

    /**
     * Add a credit card to the user's wallet.
     * Returns the saved CreditCard.
     */
    public CreditCard addCardToWallet(User user, String cardNumber) {
        if (cardNumber == null || cardNumber.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cardNumber is required");
        }

        Wallet wallet = ensureWalletForUser(user);

        // In production you should store only masked card data and tokenized values.
        CreditCard card = new CreditCard();
        card.setNumber(cardNumber);
        card.setWallet(wallet);

        return creditCardRepository.save(card);
    }

    /**
     * List cards in the user's wallet.
     */
    public List<CreditCard> listCards(User user) {
        /*
         * Wallet wallet = walletRepository.findByUser(user)
         * .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
         * "Wallet not found"));
         */
        return walletRepository.findByUser(user).map(Wallet::getCreditCards) // wallet found → return its cards
                .orElse(Collections.emptyList()); // no wallet yet → return []
        // Users without a wallet simply see no saved cards in the checkout;
        // they pay with CREDIT_CARD method which always succeeds in demo mode.
    }

    /**
     * Remove a card from the user's wallet.
     */
    public void removeCard(User user, Long cardId) {
        if (cardId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cardId is required");
        }

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found"));

        CreditCard card = wallet.getCreditCards().stream()
                .filter(c -> c.getId().equals(cardId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found in wallet"));

        // remove from wallet collection and delete
        wallet.getCreditCards().remove(card);
        walletRepository.save(wallet); // cascade = ALL + orphanRemoval = true will delete the card
    }
}
