package com.ducmanh.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "outbox_events")
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // ID của Đơn hàng (Order ID)
    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId;

    // VD: "ORDER_CREATED", "ORDER_CANCELLED"
    @Column(name = "event_type", nullable = false)
    private String eventType;

    // Chứa chuỗi JSON mang theo danh sách variantId và quantity để Catalog biết mà trừ kho
    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload;

    @Column(nullable = false)
    @Builder.Default
    private String status = "PENDING"; // PENDING hoặc PUBLISHED

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
