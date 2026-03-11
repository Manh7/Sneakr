package com.ducmanh.catalogservice.controller;

import com.ducmanh.catalogservice.dto.ApiResponse;
import com.ducmanh.catalogservice.dto.request.ImageMetadataUpdateRequest;
import com.ducmanh.catalogservice.dto.response.ProductImageResponse;
import com.ducmanh.catalogservice.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product-images")
public class ProductImageController {
    private final ProductImageService productImageService;

    @PostMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<ProductImageResponse> uploadImage(
            @PathVariable String productId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "color", required = false) String color,
            @RequestParam(value = "isPrimary", defaultValue = "false") Boolean isPrimary) throws IOException {
        return ApiResponse.<ProductImageResponse>builder()
                .result(productImageService.uploadImage(productId, file, color, isPrimary))
                .build();
    }

    @PutMapping("/{imageId}")
    public ApiResponse<ProductImageResponse> updateImageInfo(@PathVariable String imageId, @RequestBody ImageMetadataUpdateRequest request) throws IOException{
        return ApiResponse.<ProductImageResponse>builder()
                .result(productImageService.updateImageInfo(imageId, request))
                .build();
    }

    @PatchMapping("/{productId}/{imageId}/primary")
    public ApiResponse<String> setPrimaryImage(@PathVariable String productId, @PathVariable String imageId){
        productImageService.setPrimaryImage(productId, imageId);
        return ApiResponse.<String>builder()
                .result("Primary image has been updated")
                .build();
    }

    @DeleteMapping("/{imageId}")
    public ApiResponse<String> deleteImage(@PathVariable String imageId) throws IOException {
        productImageService.deleteImage(imageId);
        return ApiResponse.<String>builder()
                .result("Image has been deleted")
                .build();
    }

}
