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
import org.springframework.web.server.ResponseStatusException;
import com.example.ecommerce.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    private final ItemRepository repo;
    private final UserRepository userRepository;

    public AdminController(ItemRepository repo, UserRepository userRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
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

    @PutMapping("/users/{id}/role")
    public ResponseEntity<User> changeUserRole(@PathVariable Long id,
            @RequestBody Map<String, String> body) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        try {
            user.setRole(User.Role.valueOf(body.get("role").toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role");
        }
        return ResponseEntity.ok(userRepository.save(user));
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

        List<Order> orders = adminService.getAllOrders(page, size);

        // Break circular references exactly as OrderController.getOrderById() does
        orders.forEach(o -> {
            if (o.getUser() != null) {
                o.getUser().setPasswordHash(null);
                o.getUser().setCart(null);
                o.getUser().setOrders(null); // breaks Order→User→Orders→Order cycle
            }
            if (o.getItems() != null) {
                o.getItems().forEach(oi -> oi.setOrder(null)); // breaks OrderItem→Order cycle
            }
            if (o.getPayment() != null) {
                o.getPayment().setOrder(null); // breaks Payment→Order cycle
            }
        });

        return ResponseEntity.ok(orders);
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

    @PutMapping("/items/{itemId}/stock/add")
    public ResponseEntity<Item> addStock(@PathVariable Long itemId, @RequestParam int amount) {
        Item item = repo.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        item.setStockQuantity(item.getStockQuantity() + amount);
        return ResponseEntity.ok(repo.save(item));
    }

    @PutMapping("/items/{itemId}/stock/remove")
    public ResponseEntity<Item> removeStock(@PathVariable Long itemId, @RequestParam int amount) {
        Item item = repo.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        int newStock = item.getStockQuantity() - amount;
        if (newStock < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot reduce below zero");
        }
        item.setStockQuantity(newStock);
        return ResponseEntity.ok(repo.save(item));
    }
}
