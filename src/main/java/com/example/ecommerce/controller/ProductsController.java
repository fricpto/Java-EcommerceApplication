package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ProductDto;
import com.example.ecommerce.model.Item;
import com.example.ecommerce.repository.ItemRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductsController {

    private final ItemRepository itemRepository;

    public ProductsController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping
    public List<ProductDto> getAll() {
        return itemRepository.findAll().stream().map(item -> {
            ProductDto dto = new ProductDto();
            dto.id = item.getId();
            dto.name = item.getName();
            dto.price = item.getPrice();
            dto.image = item.getImage(); // now available on Item
            dto.category = item.getCategory();
            dto.description = item.getDescription();
            dto.tags = item.getTags() != null ? item.getTags() : List.of(); // ← real data
            dto.gender = item.getGender(); // ← real data
            System.out.println("CreateItem DTO: " + dto);
            return dto;
        }).collect(Collectors.toList());
    }
}
