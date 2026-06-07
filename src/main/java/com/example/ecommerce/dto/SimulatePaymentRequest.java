package com.example.ecommerce.dto;

import jakarta.validation.constraints.*;

public class SimulatePaymentRequest {
    @NotBlank
    @Pattern(regexp = "^(?=(?:.*\\d){16}$)[0-9 ]+$", message = "Card number must contain exactly 16 digits")
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
