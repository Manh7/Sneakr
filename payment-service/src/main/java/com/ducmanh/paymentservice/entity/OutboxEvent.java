package com.ducmanh.paymentservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "outbox_envents")
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // Dùng Order ID làm aggregateId để Kafka ném đúng vào Partition của đơn hàng đó
    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId;

    // VD: "PAYMENT_SUCCESSFUL", "PAYMENT_FAILED"
    @Column(name = "event_type", nullable = false)
    private String eventType;

    // Chứa JSON, ví dụ: {"orderId": "123", "transactionId": "abc", "status": "SUCCESS"}
    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload;

    @Column(nullable = false)
    @Builder.Default
    private String status = "PENDING"; // PENDING hoặc PUBLISHED

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
