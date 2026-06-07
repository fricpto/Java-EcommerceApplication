package com.example.ecommerce.repository;

import com.example.ecommerce.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    // Find items by category
    List<Item> findByCategory(String category);

    // Find items cheaper than a price
    List<Item> findByPriceLessThan(Double price);

    // Find items with stock available
    List<Item> findByStockQuantityGreaterThan(Integer minStock);

    // Search items by name (case-insensitive)
    List<Item> findByNameContainingIgnoreCase(String name);

}