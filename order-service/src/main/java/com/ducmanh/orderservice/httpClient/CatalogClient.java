package com.ducmanh.orderservice.httpClient;


import com.ducmanh.orderservice.dto.ApiResponse;
import com.ducmanh.orderservice.dto.response.ProductVariantResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "catalog-service", url = "http://localhost:8081/catalog")
public interface CatalogClient {
    @GetMapping("/variants")
    ApiResponse<List<ProductVariantResponse>> getVariantsByIds(@RequestParam List<String> variantIds);
}
