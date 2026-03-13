package com.ducmanh.orderservice.mapper;

import com.ducmanh.orderservice.dto.response.OrderItemResponse;
import com.ducmanh.orderservice.dto.response.OrderResponse;
import com.ducmanh.orderservice.entity.Order;
import com.ducmanh.orderservice.entity.OrderItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponse toOrderResponse(Order order);
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);
}
