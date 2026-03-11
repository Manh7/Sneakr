package com.ducmanh.cartservice.repository;

import com.ducmanh.cartservice.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<String, CartItem> {
}
