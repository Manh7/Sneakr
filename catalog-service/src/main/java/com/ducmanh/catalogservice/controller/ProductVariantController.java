package com.ducmanh.catalogservice.controller;

import com.ducmanh.catalogservice.dto.ApiResponse;
import com.ducmanh.catalogservice.dto.request.StockUpdateRequest;
import com.ducmanh.catalogservice.dto.request.VariantRequest;
import com.ducmanh.catalogservice.dto.response.ProductVariantResponse;
import com.ducmanh.catalogservice.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/variants")
public class ProductVariantController {
    private final ProductVariantService productVariantService;

    @PostMapping("/{productId}")
    public ApiResponse<ProductVariantResponse> createProductVariant(@PathVariable String productId, @RequestBody VariantRequest request){
        return ApiResponse.<ProductVariantResponse>builder()
                .result(productVariantService.createProductVariant(productId, request))
                .build();
    }

    @PutMapping("/{variantId}")
    public ApiResponse<ProductVariantResponse> updateProductVariant(@PathVariable String variantId, @RequestBody VariantRequest request) {
        return ApiResponse.<ProductVariantResponse>builder()
                .result(productVariantService.updateVariant(variantId, request))
                .build();
    }

    @PatchMapping("/{variantId}/stock")
    public ApiResponse<ProductVariantResponse> updateStock(@PathVariable String variantId, @RequestBody StockUpdateRequest request){
        return ApiResponse.<ProductVariantResponse>builder()
                .result(productVariantService.updateStock(variantId, request))
                .build();
    }

    @DeleteMapping("/{variantId}")
    public ApiResponse<String> deleteProductVariant(@PathVariable String variantId) {
        productVariantService.deleteVariant(variantId);
        return ApiResponse.<String>builder()
                .result("Product variant has been deleted")
                .build();
    }

    @GetMapping
    public ApiResponse<List<ProductVariantResponse>> getVariantsByIds(@RequestParam List<String> variantIds){
        return ApiResponse.<List<ProductVariantResponse>>builder()
                .result(productVariantService.getVariantsByIds(variantIds))
                .build();
    }

}
