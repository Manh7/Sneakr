package com.ducmanh.cartservice.service;

import com.ducmanh.cartservice.dto.request.CartItemRequest;
import com.ducmanh.cartservice.dto.request.CartItemUpdateRequest;
import com.ducmanh.cartservice.dto.response.CartItemResponse;
import com.ducmanh.cartservice.dto.response.CartResponse;
import com.ducmanh.cartservice.dto.response.ProductVariantResponse;
import com.ducmanh.cartservice.entity.Cart;
import com.ducmanh.cartservice.entity.CartItem;
import com.ducmanh.cartservice.httpClient.CatalogClient;
import com.ducmanh.cartservice.httpClient.IdentityClient;
import com.ducmanh.cartservice.mapper.CartMapper;
import com.ducmanh.cartservice.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final CatalogClient catalogClient;
    private final IdentityClient identityClient;

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

        // 2. Gọi sang Catalog Service để lấy thông tin mới nhất
        List<ProductVariantResponse> catalogData = catalogClient.getVariantsByIds(variantIds).getResult();

        // 3. Chuyển list thành Map<variantId, Data> để tìm kiếm siêu tốc độ O(1)
        Map<String, ProductVariantResponse> catalogMap = catalogData.stream()
                .collect(Collectors.toMap(ProductVariantResponse::getId, dto -> dto));

        // 4. Nhào nặn dữ liệu (Gom DB cục bộ + DB Catalog lại với nhau)
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<CartItemResponse> itemResponses = new ArrayList<>();

        for (CartItem item : cart.getItems()) {
            CartItemResponse response = cartMapper.toCartItemResponse(item);
            ProductVariantResponse catalogInfo = catalogMap.get(item.getVariantId());

            if (catalogInfo != null) {
                // Đắp thông tin từ Catalog vào
                response.setProductName(catalogInfo.getProductName());
                response.setSize(catalogInfo.getSize());
                response.setColor(catalogInfo.getColor());
                response.setImageUrl(catalogInfo.getPrimaryImageUrl());
                response.setUnitPrice(catalogInfo.getFinalPrice());
                response.setCurrentStock(catalogInfo.getStockQuantity());

                // Tính tiền cho món này (subTotal = Giá x Số lượng)
                BigDecimal quantityDecimal = new BigDecimal(item.getQuantity());
                BigDecimal subTotal = catalogInfo.getFinalPrice().multiply(quantityDecimal);
                response.setSubTotal(subTotal);

                // Cộng dồn vào tổng tiền giỏ hàng
                totalPrice = totalPrice.add(subTotal);
            } else {
                // Nếu Catalog không trả về (có thể Admin đã xóa giày đó khỏi DB)
                // Ta gán cảnh báo hoặc giá bằng 0
                response.setProductName("Sản phẩm không còn tồn tại hoặc ngừng kinh doanh");
                response.setUnitPrice(BigDecimal.ZERO);
                response.setSubTotal(BigDecimal.ZERO);
            }
            itemResponses.add(response);
        }

        return CartResponse.builder()
                .id(cart.getId())
                .userId(userId)
                .items(itemResponses)
                .totalPrice(totalPrice)
                .build();

    }

    // Thêm vào giỏ hàng
    @Transactional
    public CartResponse addCartItem(String userId, CartItemRequest request){
        Cart cart = getOrCreateCart(userId);

        // 1. Gọi Catalog check xem kho có đủ hàng không
        List<ProductVariantResponse> checkStockData = catalogClient.getVariantsByIds(List.of(request.getVariantId())).getResult();
        if (checkStockData.isEmpty()) {
            throw new RuntimeException("Sản phẩm không tồn tại trong hệ thống");
        }
        ProductVariantResponse catalogInfo = checkStockData.get(0);

        // 2. Tìm xem giày này đã có trong giỏ chưa
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getVariantId().equals(request.getVariantId()))
                .findFirst()
                .orElse(null);

        int newQuantity = request.getQuantity();
        if (existingItem != null) {
            newQuantity += existingItem.getQuantity();
        }

        // 3. Kiểm tra số lượng sau khi cộng dồn có vượt quá tồn kho không
        if (newQuantity > catalogInfo.getStockQuantity()) {
            throw new RuntimeException("Xin lỗi, kho chỉ còn " + catalogInfo.getStockQuantity() + " sản phẩm.");
        }

        // 4. Lưu vào DB
        if (existingItem != null) {
            existingItem.setQuantity(newQuantity);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .variantId(request.getVariantId())
                    .quantity(request.getQuantity())
                    .build();
            cart.addItem(newItem);
        }

        cartRepository.save(cart);

        // Trả về Giỏ hàng đầy đủ chi tiết bằng cách gọi lại hàm getCart
        return getCart(userId);
    }

    // Cập nhật số lượng
    @Transactional
    public CartResponse updateItemQuantity(String userId, String variantId, CartItemUpdateRequest request) {
        Cart cart = getOrCreateCart(userId);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getVariantId().equals(variantId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Sản phẩm không có trong giỏ hàng"));

        // Check kho bên Catalog
        List<ProductVariantResponse> checkStockData = catalogClient.getVariantsByIds(List.of(variantId)).getResult();
        if (!checkStockData.isEmpty()) {
            ProductVariantResponse catalogInfo = checkStockData.get(0);
            if (request.getQuantity() > catalogInfo.getStockQuantity()) {
                throw new RuntimeException("Xin lỗi, kho chỉ còn " + catalogInfo.getStockQuantity() + " sản phẩm.");
            }
        }

        item.setQuantity(request.getQuantity());
        cartRepository.save(cart);

        return getCart(userId);
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
