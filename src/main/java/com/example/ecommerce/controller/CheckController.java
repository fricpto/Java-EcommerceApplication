package com.example.ecommerce.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.service.CheckUserAndCardService;

@RestController
@RequestMapping("/api/check")
public class CheckController {
    private final CheckUserAndCardService service;

    public CheckController(CheckUserAndCardService service) {
        this.service = service;
    }

    @GetMapping
    public void check(@RequestParam String email, @RequestParam String cardNumber) {
        service.checkUserAndCard(email, cardNumber);
    }

}
