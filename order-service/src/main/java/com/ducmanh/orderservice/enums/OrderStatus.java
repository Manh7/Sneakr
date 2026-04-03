package com.ducmanh.orderservice.enums;

public enum OrderStatus {
    PENDING_VALIDATION, // Vừa tạo đơn, đang chờ Catalog xác nhận có đủ tồn kho không
    STOCK_CONFIRMED, // Catalog đã giữ kho thành công, chuyển sang chờ khách thanh toán
    STOCK_REJECTED, // Catalog báo hết hàng -> Đơn thất bại
    PENDING_PAYMENT, // Đang đợi khách thanh toán (VNPay/Momo)
    PAID, // Khách đã thanh toán xong
    CANCELLED, // Đơn bị hủy (Do khách tự hủy hoặc quá hạn thanh toán)
    DELIVERED // Đã giao hàng thành công
}
