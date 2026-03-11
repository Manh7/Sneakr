package com.ducmanh.catalogservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantRequest {
    @NotBlank
    private String sku;

    @NotBlank
    private String size;

    @NotBlank
    private String color;

    private BigDecimal priceAdjustment;

    @NotNull(message = "Số lượng tồn kho không được để trống")
    @Min(value = 0, message = "Tồn kho không được âm")
    private Integer stockQuantity;

}
