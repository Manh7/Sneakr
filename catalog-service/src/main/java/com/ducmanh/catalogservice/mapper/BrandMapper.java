package com.ducmanh.catalogservice.mapper;

import com.ducmanh.catalogservice.dto.request.BrandRequest;
import com.ducmanh.catalogservice.dto.response.BrandResponse;
import com.ducmanh.catalogservice.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    Brand toBrand (BrandRequest request);
    BrandResponse toBrandResponse (Brand brand);

    void updateBrand(BrandRequest request, @MappingTarget Brand brand);
}
