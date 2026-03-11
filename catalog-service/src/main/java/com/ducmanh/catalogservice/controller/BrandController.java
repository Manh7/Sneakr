package com.ducmanh.catalogservice.controller;

import com.ducmanh.catalogservice.dto.ApiResponse;
import com.ducmanh.catalogservice.dto.request.BrandRequest;
import com.ducmanh.catalogservice.dto.response.BrandResponse;
import com.ducmanh.catalogservice.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    // For user
    @GetMapping
    public ApiResponse<List<BrandResponse>> getAllBrands(){
        return ApiResponse.<List<BrandResponse>>builder()
                .result(brandService.getAllBrands())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<BrandResponse> getBrandById(@PathVariable String id){
        return ApiResponse.<BrandResponse>builder()
                .result(brandService.getBrandById(id))
                .build();
    }

    // For admin
    @PostMapping
    public ApiResponse<BrandResponse> createBrand(@RequestBody @Valid BrandRequest request){
        return ApiResponse.<BrandResponse>builder()
                .result(brandService.createBrand(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<BrandResponse> updateBrand(@PathVariable String id, @RequestBody @Valid BrandRequest request){
        return ApiResponse.<BrandResponse>builder()
                .result(brandService.updateBrand(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteBrand(@PathVariable String id){
        brandService.deleteBrand(id);
        return ApiResponse.<String>builder()
                .result("Brand has been deleted")
                .build();
    }
}
