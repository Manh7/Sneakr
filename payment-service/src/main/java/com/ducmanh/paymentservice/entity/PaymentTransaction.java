package com.ducmanh.paymentservice.entity;

import com.ducmanh.paymentservice.enums.PaymentMethod;
import com.ducmanh.paymentservice.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payment_transactions")
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // ID của Đơn hàng (Bắt buộc phải có để biết thanh toán cho đơn nào)
    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    // Mã giao dịch trả về từ VNPay/MoMo (Cực kỳ quan trọng để đối soát cuối tháng)
    // Ví dụ VNPay gọi là vnp_TransactionNo
    @Column(name = "transaction_reference")
    private String transactionReference;

    // Nếu thanh toán thất bại, lưu lại lý do (VD: "Khách hàng hủy giao dịch", "Số dư không đủ")
    @Column(name = "error_message")
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
