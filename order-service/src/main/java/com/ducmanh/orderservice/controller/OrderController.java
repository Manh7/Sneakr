package com.ducmanh.orderservice.controller;

import com.ducmanh.orderservice.dto.ApiResponse;
import com.ducmanh.orderservice.dto.request.OrderCreateRequest;
import com.ducmanh.orderservice.dto.request.OrderStatusUpdateRequest;
import com.ducmanh.orderservice.dto.response.OrderResponse;
import com.ducmanh.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    // Tạo đơn hàng
    @PostMapping("/{userId}")
    public ApiResponse<OrderResponse> createOrder(@PathVariable String userId, @RequestBody OrderCreateRequest request){
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.createOrder(userId, request))
                .build();
    }

    // Xem danh sách đơn hàng của tôi
    @GetMapping("/my-orders/{userId}")
    public ApiResponse<List<OrderResponse>> getMyOrders(@PathVariable String userId){
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getMyOrders(userId))
                .build();
    }

    // Xem chi tiết đơn hàng
    @GetMapping("/{userId}/{orderId}")
    public ApiResponse<OrderResponse> getOrderById(@PathVariable String orderId, @PathVariable String userId){
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.getOrderById(orderId, userId))
                .build();
    }

    // Khách hàng hủy đơn
    @PutMapping("/cancel/{userId}/{orderId}")
    public ApiResponse<OrderResponse> cancelOrder(@PathVariable String userId, @PathVariable String orderId){
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.cancelOrder(orderId, userId))
                .build();
    }

    // For admin
    @GetMapping
    public ApiResponse<List<OrderResponse>> getAllOrders(){
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getAllOrders())
                .build();
    }

    @PatchMapping("/{orderId}/status")
    public ApiResponse<OrderResponse> updateOrderStatus(@PathVariable String orderId, @RequestBody OrderStatusUpdateRequest request){
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.updateOrderStatus(orderId, request))
                .build();
    }



}
