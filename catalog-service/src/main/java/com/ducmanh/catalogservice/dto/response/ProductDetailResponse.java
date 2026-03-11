package com.ducmanh.catalogservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailResponse {
    private String id;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private String categoryName;
    private String brandName;

    private List<ProductVariantResponse> variants;
    private List<ProductImageResponse> images;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
