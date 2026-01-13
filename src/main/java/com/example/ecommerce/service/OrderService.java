package com.example.ecommerce.service;

import com.example.ecommerce.model.*;
import com.example.ecommerce.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Transactional
    public Order placeOrder(User user) {
        log.info("Placing order for user id {}", user.getId());

        // load cart using your repository signature
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart not found"));

        List<CartItem> items = cart.getItems();
        if (items == null || items.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty, cannot place order");
        }

        // build order (compute totals BEFORE saving)
        Order order = new Order();
        order.setUser(user);
        order.setCreatedAt(LocalDateTime.now());
        order.setOrderDate(LocalDateTime.now()); // if your Order has orderDate
        order.setStatus(Order.Status.PENDING);

        // ensure items collection
        if (order.getItems() == null) {
            order.setItems(new java.util.ArrayList<>());
        }

        // compute total while converting cart items -> order items
        double total = 0.0;
        for (CartItem ci : items) {
            Item product = ci.getItem();
            int qty = ci.getQuantity();

            // validations (same as before)
            if (product == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart contains invalid item");
            if (qty <= 0)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid item quantity");
            if (product.getStockQuantity() < qty)
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Not enough stock for item: " + product.getName());

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setItem(product);
            oi.setQuantity(qty);
            oi.setPrice(product.getPrice());

            order.getItems().add(oi);

            // accumulate total
            total += product.getPrice() * qty;

            // decrement stock and persist product
            product.setStockQuantity(product.getStockQuantity() - qty);
            itemRepository.save(product);
        }

        // set total before persisting the order
        order.setTotalAmount(total);

        // now persist order (cascade should persist OrderItem rows)
        Order saved = orderRepository.save(order);

        // persist order (assumes cascade on order.items will persist OrderItem rows)
        // Order saved = orderRepository.save(order);

        // clear cart items
        cart.getItems().clear();
        cartRepository.save(cart);

        log.info("Order {} placed for user {}", saved.getId(), user.getEmail());
        return saved;
    }

    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }

    public Order cancelOrder(User user, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot cancel someone else's order");
        }
        if (order.getStatus() == Order.Status.PAID) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot cancel a paid order");
        }

        order.setStatus(Order.Status.CANCELLED);
        return orderRepository.save(order);
    }

    @Transactional
    public Order markOrderAsPaid(User user, Long orderId, Payment.Method requestedMethod, Long requestedCardId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }

        if (order.getStatus() == Order.Status.PAID) {
            return order;
        }

        // Decide method
        Payment.Method methodToUse = Payment.Method.CREDIT_CARD;
        if (requestedMethod != null) {
            methodToUse = requestedMethod;
        } else if (walletRepository.findByUser(user).isPresent()) {
            methodToUse = Payment.Method.WALLET;
        }

        // If WALLET: pick a credit card from the wallet (no balance field required)
        CreditCard cardToCharge = null;
        if (methodToUse == Payment.Method.WALLET) {
            Wallet wallet = walletRepository.findByUser(user)
                    .orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No wallet available for user"));

            List<CreditCard> cards = wallet.getCreditCards();
            if (cards == null || cards.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wallet has no credit cards");
            }

            if (requestedCardId != null) {
                cardToCharge = cards.stream()
                        .filter(c -> c.getId().equals(requestedCardId))
                        .findFirst()
                        .orElseThrow(
                                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Card not found in wallet"));
            } else {
                if (cards.size() == 1) {
                    cardToCharge = cards.get(0);
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Multiple cards in wallet; provide cardId in request");
                }
            }

            // simulate card authorization using cardToCharge (always succeed in demo)
            // you can log card last4 for audit:
            log.info("Charging wallet card id={} for userId={} orderId={}", cardToCharge.getId(), user.getId(),
                    orderId);
        }

        // create and persist payment
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setMethod(methodToUse); // WALLET or CREDIT_CARD
        payment.setStatus(Payment.Status.AUTHORIZED); // simulate success

        // optional: store card metadata on payment if Payment model supports it
        // e.g., payment.setCardLast4(cardToCharge != null ? cardToCharge.getLast4() :
        // null);

        Payment savedPayment = paymentRepository.save(payment);

        // attach payment and mark paid
        order.setStatus(Order.Status.PAID);
        try {
            order.setPayment(savedPayment);
        } catch (Throwable ignored) {
        }
        return orderRepository.save(order);
    }

}
