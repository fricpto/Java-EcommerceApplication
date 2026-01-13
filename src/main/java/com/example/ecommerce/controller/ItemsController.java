package com.example.ecommerce.controller;

import com.example.ecommerce.model.Item;
import com.example.ecommerce.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemsController {

    @Autowired
    private ItemRepository itemRepository;

    // List all items
    @GetMapping
    public ResponseEntity<List<Item>> list() {
        return ResponseEntity.ok(itemRepository.findAll());
    }

    // Get item details by id
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItem(@PathVariable Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        return ResponseEntity.ok(item);
    }
}
