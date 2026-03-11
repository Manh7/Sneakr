package com.ducmanh.catalogservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantResponse {
    private String id;
    private String sku;
    private String size;
    private String color;
    private Integer stockQuantity;
    private Integer reservedStock;

    // Giá cuối cùng = basePrice (của Product) + priceAdjustment (của Variant)
    private BigDecimal finalPrice;

    private String productName;
    private String primaryImageUrl;
}
