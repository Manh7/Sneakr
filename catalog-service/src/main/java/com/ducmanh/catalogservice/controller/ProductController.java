package com.ducmanh.catalogservice.controller;

import com.ducmanh.catalogservice.dto.ApiResponse;
import com.ducmanh.catalogservice.dto.request.ProductCreateRequest;
import com.ducmanh.catalogservice.dto.request.ProductUpdateRequest;
import com.ducmanh.catalogservice.dto.response.ProductDetailResponse;
import com.ducmanh.catalogservice.dto.response.ProductListResponse;
import com.ducmanh.catalogservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    // For user

    @GetMapping("/{id}")
    public ApiResponse<ProductDetailResponse> getProductDetail(@PathVariable String id) {
        return ApiResponse.<ProductDetailResponse>builder()
                .result(productService.getProductDetail(id))
                .build();
    }

    @GetMapping
    public ApiResponse<Page<ProductListResponse>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String brandId,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        return ApiResponse.<Page<ProductListResponse>>builder()
                .result(productService.getProducts(page, size, keyword, brandId, categoryId, minPrice, maxPrice))
                .build();
    }



    // For admin

    @PostMapping
    public ApiResponse<ProductDetailResponse> createProduct(@RequestBody ProductCreateRequest request){
        return ApiResponse.<ProductDetailResponse>builder()
                .result(productService.createProduct(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductDetailResponse> updateProduct(@PathVariable String id, @RequestBody ProductUpdateRequest request){
        return ApiResponse.<ProductDetailResponse>builder()
                .result(productService.updateProduct(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteProduct(@PathVariable String id){
        productService.deleteProduct(id);
        return ApiResponse.<String>builder()
                .result("Product has been deleted")
                .build();
    }
}
