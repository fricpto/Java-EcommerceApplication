package com.example.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record ItemDto(
                @NotBlank String name,
                @NotBlank String description,
                @PositiveOrZero double price,
                @Min(0) int stockQuantity,
                @NotBlank String category,
                String image, // new optional field, can be null
                String gender, // optional: e.g., "male", "female", "unisex"
                List<String> tags // optional list of tags
) {
}
