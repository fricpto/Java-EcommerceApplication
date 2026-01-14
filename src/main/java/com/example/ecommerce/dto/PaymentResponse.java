package com.example.ecommerce.dto;

public class PaymentResponse {
    public Long paymentId;
    public String status;
    public Double amount;
    public String last4;
    public Integer expiryMonth;
    public Integer expiryYear;

    public PaymentResponse(Long paymentId, String status, Double amount, String last4, Integer expiryMonth,
            Integer expiryYear) {
        this.paymentId = paymentId;
        this.status = status;
        this.amount = amount;
        this.last4 = last4;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
    }
}
