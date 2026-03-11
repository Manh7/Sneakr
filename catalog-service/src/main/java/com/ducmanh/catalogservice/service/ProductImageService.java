package com.ducmanh.catalogservice.service;

import com.ducmanh.catalogservice.dto.request.ImageMetadataUpdateRequest;
import com.ducmanh.catalogservice.dto.response.ProductImageResponse;
import com.ducmanh.catalogservice.entity.Product;
import com.ducmanh.catalogservice.entity.ProductImage;
import com.ducmanh.catalogservice.mapper.ProductImageMapper;
import com.ducmanh.catalogservice.repository.ProductImageRepository;
import com.ducmanh.catalogservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductImageService {
    private final ProductImageRepository productImageRepository;
    private final CloudinaryService cloudinaryService;
    private final ProductRepository productRepository;
    private final ProductImageMapper productImageMapper;

    @Transactional
    public ProductImageResponse uploadImage(String productId, MultipartFile file, String color, Boolean isPrimary) throws IOException{
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + productId));

        // 1. Gọi CloudinaryService để đẩy file lên mây
        String imageUrl = cloudinaryService.uploadImage(file);

        // 2. Logic: Nếu cờ isPrimary là true, phải đưa các ảnh cũ về false
        if (Boolean.TRUE.equals(isPrimary)) {
            resetPrimaryImages(productId);
        }

        // 3. Lưu vào Database
        ProductImage image = ProductImage.builder()
                .product(product)
                .imageUrl(imageUrl)
                .color(color)
                .isPrimary(isPrimary != null ? isPrimary : false)
                .build();

        image = productImageRepository.save(image);
        return productImageMapper.toProductImageResponse(image);
    }

    // --- Hàm Helper hỗ trợ tắt cờ isPrimary của các ảnh khác ---
    private void resetPrimaryImages(String productId) {
        List<ProductImage> existingImages = productImageRepository.findByProductId(productId);
        for (ProductImage img : existingImages) {
            if (Boolean.TRUE.equals(img.getIsPrimary())) {
                img.setIsPrimary(false);
            }
        }
        productImageRepository.saveAll(existingImages);
    }

    @Transactional
    public ProductImageResponse updateImageInfo(String imageId, ImageMetadataUpdateRequest request) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ảnh với ID: " + imageId));

        if (request.getColor() != null) {
            image.setColor(request.getColor());
        }

        if (Boolean.TRUE.equals(request.getIsPrimary()) && !Boolean.TRUE.equals(image.getIsPrimary())) {
            resetPrimaryImages(image.getProduct().getId());
            image.setIsPrimary(true);
        } else if (request.getIsPrimary() != null) {
            image.setIsPrimary(request.getIsPrimary());
        }

        image = productImageRepository.save(image);
        return productImageMapper.toProductImageResponse(image);
    }

    @Transactional
    public void setPrimaryImage(String productId, String imageId){
        ProductImage newPrimaryImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ảnh với ID: " + imageId));

        if (!newPrimaryImage.getProduct().getId().equals(productId)) {
            throw new RuntimeException("Ảnh này không thuộc về sản phẩm có ID: " + productId);
        }

        resetPrimaryImages(productId);

        newPrimaryImage.setIsPrimary(true);
        productImageRepository.save(newPrimaryImage);
    }

    @Transactional
    public void deleteImage(String imageId) throws IOException{
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ảnh với ID: " + imageId));

        // Bóc tách public_id và gọi Cloudinary xóa file vật lý
        try {
            String publicId = cloudinaryService.extractPublicId(image.getImageUrl());
            cloudinaryService.deleteImage(publicId);
            log.info("Đã xóa file ảnh trên Cloudinary: {}", publicId);
        } catch (Exception e) {
            log.warn("Lỗi khi xóa ảnh trên Cloudinary (có thể ảnh đã bị xóa trước đó): {}", e.getMessage());
            // Vẫn tiếp tục thực hiện xóa trong Database để tránh rác DB
        }

        productImageRepository.delete(image);
    }

}
