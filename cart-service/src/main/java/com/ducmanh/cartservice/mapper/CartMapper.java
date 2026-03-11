package com.ducmanh.cartservice.mapper;

import com.ducmanh.cartservice.dto.response.CartItemResponse;
import com.ducmanh.cartservice.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "size", ignore = true)
    @Mapping(target = "color", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "unitPrice", ignore = true)
    @Mapping(target = "subTotal", ignore = true)
    @Mapping(target = "currentStock", ignore = true)
    CartItemResponse toCartItemResponse(CartItem cartItem);
}
