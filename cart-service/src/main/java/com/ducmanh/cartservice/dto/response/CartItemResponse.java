package com.ducmanh.cartservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private String variantId;
    private Integer quantity;

    private String productName;
    private String size;
    private String color;
    private String imageUrl;
    private BigDecimal unitPrice; // Giá thời điểm hiện tại
    private BigDecimal subTotal;
    private Integer currentStock; // Số lượng tồn kho hiện tại
}
