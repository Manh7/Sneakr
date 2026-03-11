package com.ducmanh.catalogservice.dto.request;

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
public class ProductCreateRequest {
    @NotBlank
    private String name;

    private String description;

    @NotNull
    private BigDecimal basePrice;

    @NotBlank
    private String categoryId;

    @NotBlank
    private String brandId;
}
