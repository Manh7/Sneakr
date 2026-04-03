package com.ducmanh.orderservice.dto.request;

import com.ducmanh.orderservice.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusUpdateRequest {
    @NotNull
    private OrderStatus status;

    private String note;
}
