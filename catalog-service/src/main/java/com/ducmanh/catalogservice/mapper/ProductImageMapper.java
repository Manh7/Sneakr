package com.ducmanh.catalogservice.mapper;

import com.ducmanh.catalogservice.dto.response.ProductImageResponse;
import com.ducmanh.catalogservice.entity.ProductImage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {
    ProductImageResponse toProductImageResponse(ProductImage productImage);
}
