package com.ducmanh.orderservice.dto.request;

import com.ducmanh.orderservice.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateRequest {

    private String recipientName;

    private String ShippingAddress;

    private String phoneNumber;

    private PaymentMethod paymentMethod;

    private String note;

    private List<OrderItemRequest> items;
}
