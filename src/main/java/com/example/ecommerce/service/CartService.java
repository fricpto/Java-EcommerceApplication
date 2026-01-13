package com.example.ecommerce.service;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Item;
import com.example.ecommerce.model.User;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ItemRepository;
import com.example.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    public Cart getCartByUser(User user) {
        return cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
    }

    public Cart addItemToCart(User user, Long itemId, int quantity) {
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        // Check if item already exists in cart
        CartItem existingItem = cart.getItems().stream()
                .filter(ci -> ci.getItem().getId().equals(itemId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setItem(item);
            cartItem.setQuantity(quantity);
            cartItem.setCart(cart);
            cart.getItems().add(cartItem);
        }

        return cartRepository.save(cart);
    }

    public Cart removeItemFromCart(User user, Long itemId) {
        Cart cart = getCartByUser(user);

        boolean removed = cart.getItems().removeIf(ci -> ci.getItem().getId().equals(itemId));
        if (!removed) {
            throw new RuntimeException("Item with ID " + itemId + " not found in cart");
        }

        return cartRepository.save(cart);
    }

    public Cart clearCart(User user) {
        Cart cart = getCartByUser(user);

        // if (cart.getItems().isEmpty()) {
        // throw new RuntimeException("Cart is already empty");
        // }

        cart.getItems().clear();
        return cartRepository.save(cart);
    }

    public Cart updateItemQuantity(User user, Long itemId, int quantity) {
        Cart cart = getCartByUser(user);

        CartItem cartItem = cart.getItems().stream()
                .filter(ci -> ci.getItem().getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found in cart"));

        if (quantity <= 0) {
            cart.getItems().remove(cartItem);
        } else {
            cartItem.setQuantity(quantity);
        }

        return cartRepository.save(cart);
    }

}