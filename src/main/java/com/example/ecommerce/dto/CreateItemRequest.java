package com.example.ecommerce.dto;

public class CreateItemRequest {
    public String name;
    public String description;
    public Double price;
    public Integer stockQuantity;
    public String category;
    public String image; // optional external URL
}
