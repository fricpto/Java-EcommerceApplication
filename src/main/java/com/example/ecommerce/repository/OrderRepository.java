package com.example.ecommerce.repository;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Find all orders for a user
    List<Order> findByUser(User user);

    // Find all orders
    List<Order> findAll();

    // Find orders by status
    List<Order> findByStatus(Order.Status status);

    Optional<Order> findByIdAndUser(Long id, User user);

    // in OrderRepository
    @Query("select case when count(oi) > 0 then true else false end " +
            "from Order o join o.items oi where oi.item.id = :itemId")
    boolean existsByItemsItemId(@Param("itemId") Long itemId);

}