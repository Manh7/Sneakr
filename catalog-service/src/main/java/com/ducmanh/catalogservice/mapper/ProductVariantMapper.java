package com.ducmanh.catalogservice.mapper;

import com.ducmanh.catalogservice.dto.request.VariantRequest;
import com.ducmanh.catalogservice.dto.response.ProductVariantResponse;
import com.ducmanh.catalogservice.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface ProductVariantMapper {

    // Gọi hàm custom bên dưới để tính giá finalPrice
    @Mapping(source = "product.name", target = "productName")
    @Mapping(target = "finalPrice", expression = "java(calculateFinalPrice(variant))")
    @Mapping(target = "primaryImageUrl", expression = "java(getPrimaryImage(variant))")
    ProductVariantResponse toResponse(ProductVariant variant);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "reservedStock", ignore = true) // Luôn bằng 0 khi mới tạo
    ProductVariant toEntity(VariantRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "reservedStock", ignore = true)
    void updateEntityFromRequest(VariantRequest request, @MappingTarget ProductVariant variant);

    // --- Hàm Custom hỗ trợ tính giá ---
    default BigDecimal calculateFinalPrice(ProductVariant variant){
        if (variant.getProduct() == null || variant.getProduct().getBasePrice() == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal baserPrice = variant.getProduct().getBasePrice();
        BigDecimal priceAdjustment = variant.getPriceAdjustment() != null ? variant.getPriceAdjustment() : BigDecimal.ZERO;
        return baserPrice.add(priceAdjustment);
    }

    // Hàm Custom hỗ trợ tìm ảnh chính
    default String getPrimaryImage(ProductVariant variant) {
        if (variant.getProduct() == null || variant.getProduct().getImages() == null) {
            return null;
        }
        return variant.getProduct().getImages().stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                .map(img -> img.getImageUrl())
                .findFirst()
                .orElse(null);
    }
}
