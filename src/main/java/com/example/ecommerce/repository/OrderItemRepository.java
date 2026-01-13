package com.example.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerce.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Modifying
    @Transactional
    @Query("delete from OrderItem oi where oi.item.id = :itemId")
    void deleteByItemId(@Param("itemId") Long itemId);

    @Query("select case when count(oi) > 0 then true else false end from OrderItem oi where oi.item.id = :itemId")
    boolean existsByItemId(@Param("itemId") Long itemId);
}
