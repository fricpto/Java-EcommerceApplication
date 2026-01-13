package com.example.ecommerce.dto;

public class PayRequest {
    public String method; // "WALLET" or "CREDIT_CARD"
    public Long cardId; // optional: id of CreditCard in the user's wallet
}
