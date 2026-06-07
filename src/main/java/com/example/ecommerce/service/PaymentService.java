package com.example.ecommerce.service;

import com.example.ecommerce.dto.PaymentResponse;
import com.example.ecommerce.dto.SimulatePaymentRequest;
import com.example.ecommerce.model.CreditCard;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.PaymentRecord;
import com.example.ecommerce.repository.OrderRepository;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ecommerce.repository.CreditCardRepository;
import com.example.ecommerce.repository.WalletRepository;

import jakarta.transaction.Transactional;

import com.example.ecommerce.repository.PaymentRecordRepository;

import java.util.Optional;

@Service
public class PaymentService {

    private OrderRepository orderRepository;

    private CreditCardRepository creditCardRepository;

    private WalletRepository walletRepository;

    private PaymentRecordRepository paymentRecordRepository;

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

    public PaymentService(PaymentRecordRepository paymentRecordRepository, CreditCardRepository creditCardRepository,
            WalletRepository walletRepository) {
        this.paymentRecordRepository = paymentRecordRepository;
        this.creditCardRepository = creditCardRepository;
        this.walletRepository = walletRepository;
    }

    @Transactional
    public PaymentResponse simulatePayment(SimulatePaymentRequest req, Long walletId) {
        // Validate expiry
        int nowYear = Instant.now().atZone(java.time.ZoneId.systemDefault()).getYear();
        int nowMonth = Instant.now().atZone(java.time.ZoneId.systemDefault()).getMonthValue();
        if (req.expiryYear < nowYear || (req.expiryYear == nowYear && req.expiryMonth < nowMonth)) {
            throw new IllegalArgumentException("Card expiry is in the past");
        }

        // Normalize card number and compute last4
        String digits = req.cardNumber.replaceAll("\\s+", "");
        String last4 = digits.length() >= 4 ? digits.substring(digits.length() - 4) : digits;

        // Resolve wallet by the walletId passed from controller (server-side)
        com.example.ecommerce.model.Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        // Persist card display info (mask PAN; do NOT store CVV)
        Optional<CreditCard> existing = creditCardRepository.findByWalletAndLast4(wallet, last4);

        CreditCard card = existing.orElseGet(() -> {
            CreditCard c = new CreditCard();
            c.setNumber("**** **** **** " + last4);
            c.setLast4(last4);
            c.setExpiryMonth(req.expiryMonth);
            c.setExpiryYear(req.expiryYear);
            c.setWallet(wallet);
            return creditCardRepository.save(c);
        });

        CreditCard savedCard = creditCardRepository.save(card);

        // Simulate authorization logic (deterministic rule for testing)
        String status = "SIMULATED";
        if (last4.endsWith("0")) {
            status = "DECLINED";
        }

        PaymentRecord pr = new PaymentRecord();
        pr.setWalletId(walletId); // use walletId parameter
        pr.setCardId(savedCard.getId()); // use saved card id
        pr.setAmount(req.amount);
        pr.setStatus(status);
        pr.setCreatedAt(Instant.now());

        PaymentRecord savedPr = paymentRecordRepository.save(pr);

        return new PaymentResponse(
                savedPr.getId(),
                status,
                savedPr.getAmount(),
                savedCard.getLast4(),
                savedCard.getExpiryMonth(),
                savedCard.getExpiryYear());
    }

}
