package com.ducmanh.cartservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest {

    @NotBlank
    private String variantId;

    @NotNull
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer quantity;
}
