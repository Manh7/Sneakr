package com.ducmanh.cartservice.service;

import com.ducmanh.cartservice.dto.response.CartResponse;
import com.ducmanh.cartservice.entity.Cart;
import com.ducmanh.cartservice.entity.CartItem;
import com.ducmanh.cartservice.mapper.CartMapper;
import com.ducmanh.cartservice.repository.CartItemRepository;
import com.ducmanh.cartservice.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;

    // HELPER: Lấy Giỏ hàng hoặc Tạo mới nếu chưa có
    // =========================================================================
    private Cart getOrCreateCart(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder().userId(userId).build();
                    return cartRepository.save(newCart);
                });
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(String userId){
        Cart cart = getOrCreateCart(userId);
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            return CartResponse.builder()
                    .id(cart.getId())
                    .userId(userId)
                    .items(new ArrayList<>())
                    .totalPrice(BigDecimal.ZERO)
                    .build();
        }

        // 1. Rút trích danh sách variantIds từ DB Giỏ hàng
        List<String> variantIds = cart.getItems().stream()
                .map(CartItem::getVariantId)
                .collect(Collectors.toList());


    }


    @Transactional
    public CartResponse removeItem(String userId, String variantId){
        Cart cart = getOrCreateCart(userId);

        cart.getItems().removeIf(item -> item.getVariantId().equals(variantId));
        cartRepository.save(cart);

        return getCart(userId);
    }


    // Xóa sạch giỏ hàng (Clear Cart)
    @Transactional
    public void clearCart(String userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().clear(); // Nhờ orphanRemoval = true, nó sẽ tự xóa các dòng trong bảng cart_items
        cartRepository.save(cart);
    }
}
