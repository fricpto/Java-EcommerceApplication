package com.example.ecommerce.dto;

public class AddItemRequest {
    private Long itemId;
    private int quantity;

    // getters and setters
    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;

    }
}

// Data Transfer Object.It’s a simple class whose only job is to carry data
// between layers (controller → service → repository).
// It usually has just fields + getters/setters, no business logic.