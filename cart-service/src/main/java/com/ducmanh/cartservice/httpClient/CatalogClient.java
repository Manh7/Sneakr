package com.ducmanh.cartservice.httpClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "catalog-service", url = "http://localhost:8081")
public interface CatalogClient {
    @GetMapping("/catalog/variants")
    List<CatalogVariantDto> getVariantsByIds(@RequestParam("ids") List<String> ids);
}
