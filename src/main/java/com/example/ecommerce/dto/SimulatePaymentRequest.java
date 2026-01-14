package com.example.ecommerce.dto;

import jakarta.validation.constraints.*;

public class SimulatePaymentRequest {
    @NotBlank
    public String cardNumber;
    @NotNull
    @Min(1)
    @Max(12)
    public Integer expiryMonth;
    @NotNull
    @Min(2023)
    public Integer expiryYear;
    @NotBlank
    @Size(min = 3, max = 4)
    public String cvv;
    @NotNull
    public Double amount;
}
