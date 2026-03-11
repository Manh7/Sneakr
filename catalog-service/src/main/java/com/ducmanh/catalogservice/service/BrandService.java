package com.ducmanh.catalogservice.service;

import com.ducmanh.catalogservice.dto.request.BrandRequest;
import com.ducmanh.catalogservice.dto.response.BrandResponse;

import java.util.List;

public interface BrandService {
    BrandResponse getBrandById (String id);
    List<BrandResponse> getAllBrands();
    BrandResponse createBrand(BrandRequest request);
    BrandResponse updateBrand(String id, BrandRequest request);
    void deleteBrand(String id);
}
