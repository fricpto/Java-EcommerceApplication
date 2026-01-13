package com.example.ecommerce.controller;

import com.example.ecommerce.model.User;
import com.example.ecommerce.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Only admins can list all users
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> all() {
        return userService.getAllUsers();
    }

    // Anyone can register (permitAll in SecurityConfig)
    @PostMapping
    public User create(@RequestBody User u) {
        return userService.createUser(u);
    }
}