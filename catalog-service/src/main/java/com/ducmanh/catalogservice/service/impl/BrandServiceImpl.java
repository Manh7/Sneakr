package com.ducmanh.catalogservice.service.impl;

import com.ducmanh.catalogservice.dto.request.BrandRequest;
import com.ducmanh.catalogservice.dto.response.BrandResponse;
import com.ducmanh.catalogservice.entity.Brand;
import com.ducmanh.catalogservice.mapper.BrandMapper;
import com.ducmanh.catalogservice.repository.BrandRepository;
import com.ducmanh.catalogservice.service.BrandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BrandServiceImpl implements BrandService {
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;


    @Override
    public BrandResponse getBrandById(String id) {
        Brand brand = brandRepository.findById(id).orElseThrow(() -> new RuntimeException("Brand not found"));
        return brandMapper.toBrandResponse(brand);
    }

    @Override
    public List<BrandResponse> getAllBrands() {
        List<BrandResponse> listBrand = brandRepository.findAll().stream().map(brandMapper::toBrandResponse).toList();
        return listBrand;
    }

    @Override
    public BrandResponse createBrand(BrandRequest request) {
        Brand brand = brandMapper.toBrand(request);
        brand = brandRepository.save(brand);
        return brandMapper.toBrandResponse(brand);
    }

    @Override
    public BrandResponse updateBrand(String id, BrandRequest request) {
        Brand brand = brandRepository.findById(id).orElseThrow(() -> new RuntimeException("Brand not found"));
        brandMapper.updateBrand(request, brand);
        brand = brandRepository.save(brand);
        return brandMapper.toBrandResponse(brand);
    }

    @Override
    public void deleteBrand(String id) {
        brandRepository.deleteById(id);
    }
}
