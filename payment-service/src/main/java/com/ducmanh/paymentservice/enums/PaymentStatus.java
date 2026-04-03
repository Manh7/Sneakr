package com.ducmanh.paymentservice.enums;

public enum PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED, // Giao dịch thất bại (Hủy ngang, sai mã OTP, thẻ hết tiền...)
    REFUNDED // Đã hoàn tiền (Dùng cho case khách trả hàng hoặc Saga rollback)
}
