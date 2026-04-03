package com.ducmanh.orderservice.repository;

import com.ducmanh.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {

    // Tìm danh sách đơn hàng của 1 user cụ thể
    List<Order> findByUserIdOrderByCreatedAtDesc(String userId);
}
