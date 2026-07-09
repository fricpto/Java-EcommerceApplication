package com.example.ecommerce.service;

import com.example.ecommerce.dto.ItemDto;
import com.example.ecommerce.model.Item;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.ItemRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ---------------- USER MANAGEMENT ----------------

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public User updateUserCredentials(Long id, String newEmail, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        if (newEmail != null && !newEmail.isEmpty()) {
            if (userRepository.findByEmail(newEmail).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
            }
            user.setEmail(newEmail);
        }

        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(newPassword));
        }
        return userRepository.save(user);
    }

    // ---------------- ITEM MANAGEMENT ----------------

    public Item addItem(Item item) {
        return itemRepository.save(item);
    }

    public Item updateItem(Item item) {
        return itemRepository.save(item);
    }

    @Transactional
    public void deleteItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));

        boolean referenced = orderRepository.existsByItemsItemId(id);
        if (referenced) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Item is referenced by existing orders and cannot be deleted");
        }
        if (orderRepository.existsByItemsItemId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Item cannot be deleted because it is referenced by existing orders. Use soft-delete or force-delete.");
        }

        itemRepository.delete(item);
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    // ---------------- ORDER MANAGEMENT ----------------

    @Transactional(readOnly = true)
    public List<Order> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), Sort.by("createdAt").descending());
        return orderRepository.findAll(pageable).getContent();
    }

    public List<Order> getOrdersByStatus(Order.Status status) {
        return orderRepository.findByStatus(status);
    }

    @Transactional
    public Item createItem(ItemDto dto) {
        Item item = new Item();
        item.setName(dto.name());
        item.setDescription(dto.description());
        item.setPrice(dto.price());
        item.setStockQuantity(dto.stockQuantity());
        item.setCategory(dto.category());
        item.setImage(dto.image());
        item.setGender(dto.gender()); // ← was missing
        item.setTags(dto.tags()); // ← was missing
        return itemRepository.save(item);
    }

    @Transactional
    public Item updateItem(Long id, ItemDto dto) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found"));
        item.setName(dto.name());
        item.setDescription(dto.description());
        item.setPrice(dto.price());
        item.setStockQuantity(dto.stockQuantity());
        item.setCategory(dto.category());
        item.setImage(dto.image()); // ← was missing
        item.setGender(dto.gender()); // ← was missing
        item.setTags(dto.tags()); // ← was missing
        return itemRepository.save(item);
    }

    public void cancelOrder(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus(Order.Status.CANCELLED);
            orderRepository.save(order);
        }
    }
}