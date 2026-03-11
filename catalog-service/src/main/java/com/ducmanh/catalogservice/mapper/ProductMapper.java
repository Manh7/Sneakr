package com.ducmanh.catalogservice.mapper;

import com.ducmanh.catalogservice.dto.request.ProductCreateRequest;
import com.ducmanh.catalogservice.dto.request.ProductUpdateRequest;
import com.ducmanh.catalogservice.dto.response.ProductDetailResponse;
import com.ducmanh.catalogservice.dto.response.ProductListResponse;
import com.ducmanh.catalogservice.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ProductImageMapper.class, ProductVariantMapper.class})
public interface ProductMapper {
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "brand.name", target = "brandName")
    ProductDetailResponse toDetailResponse(Product product);

    // 1. Trả về cho trang danh sách
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "brand.name", target = "brandName")
    // primaryImageUrl không map ở đây
    @Mapping(target = "primaryImageUrl", ignore = true)
    ProductListResponse toListResponse(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "variants", ignore = true)
    @Mapping(target = "images", ignore = true)
    Product toProduct(ProductCreateRequest request);

    // 4. Update Product
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "variants", ignore = true)
    @Mapping(target = "images", ignore = true)
    void updateEntityFromRequest(ProductUpdateRequest request, @MappingTarget Product product);
}
