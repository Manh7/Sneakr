package com.ducmanh.cartservice.controller;

import com.ducmanh.cartservice.dto.ApiResponse;
import com.ducmanh.cartservice.dto.request.CartItemRequest;
import com.ducmanh.cartservice.dto.request.CartItemUpdateRequest;
import com.ducmanh.cartservice.dto.response.CartResponse;
import com.ducmanh.cartservice.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping("/{userId}")
    public ApiResponse<CartResponse> getCart(@PathVariable String userId){
        return ApiResponse.<CartResponse>builder()
                .result(cartService.getCart(userId))
                .build();
    }

    @PostMapping("/{userId}")
    public ApiResponse<CartResponse> addCartItem(@PathVariable String userId, @RequestBody CartItemRequest request){
        return ApiResponse.<CartResponse>builder()
                .result(cartService.addCartItem(userId, request))
                .build();
    }

    @PutMapping("/items/{variantId}/{userId}")
    public ApiResponse<CartResponse> updateItemQuantity(@PathVariable String userId, @PathVariable String variantId, @RequestBody CartItemUpdateRequest request){
        return ApiResponse.<CartResponse>builder()
                .result(cartService.updateItemQuantity(userId, variantId, request))
                .build();
    }

    @DeleteMapping("/items/{variantId}/{userId}")
    public ApiResponse<CartResponse> removeCartItem(@PathVariable String variantId, @PathVariable String userId){
        return ApiResponse.<CartResponse>builder()
                .result(cartService.removeItem(userId, variantId))
                .build();
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<String> clearCart(@PathVariable String userId){
        cartService.clearCart(userId);
        return ApiResponse.<String>builder()
                .result("Cart has been cleared")
                .build();
    }



}
