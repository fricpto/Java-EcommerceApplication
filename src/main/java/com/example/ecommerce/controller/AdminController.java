package com.example.ecommerce.controller;

import com.example.ecommerce.dto.CreateItemRequest;
import com.example.ecommerce.dto.ItemDto;
import com.example.ecommerce.model.Item;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.ItemRepository;
import com.example.ecommerce.service.AdminService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    private final ItemRepository repo;

    public AdminController(ItemRepository repo) {
        this.repo = repo;
    }

    // @PostMapping("/items")
    // public ResponseEntity<Item> createItemReq(@RequestBody @Valid
    // CreateItemRequest req,@RequestBody @Valid ItemDto dto) {
    // Item item = new Item();
    // item.setName(req.name);
    // item.setDescription(req.description);
    // item.setPrice(req.price);
    // item.setStockQuantity(req.stockQuantity);
    // item.setCategory(req.category);
    // // item.setImage(req.image); // if you add image to Item
    // Item saved = repo.save(item);
    // return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    // }

    // other admin endpoints (e.g., /orders) remain here

    // USERS
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/users/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        return adminService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // ITEMS (use DTOs consistently)
    @GetMapping("/items")
    public ResponseEntity<List<Item>> getAllItems() {
        return ResponseEntity.ok(adminService.getAllItems());
    }

    @PostMapping("/items")
    public ResponseEntity<Item> createItem(@RequestBody @Valid ItemDto dto) {
        Item created = adminService.createItem(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @RequestBody @Valid ItemDto dto) {
        Item updated = adminService.updateItem(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        adminService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    // ORDERS
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(adminService.getAllOrders(page, size));
    }

    @GetMapping("/orders/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable Order.Status status) {
        return ResponseEntity.ok(adminService.getOrdersByStatus(status));
    }

    @PutMapping("/orders/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        adminService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }
}
