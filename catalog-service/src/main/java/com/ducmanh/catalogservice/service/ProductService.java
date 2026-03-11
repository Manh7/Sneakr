package com.ducmanh.catalogservice.service;

import com.ducmanh.catalogservice.dto.request.ProductCreateRequest;
import com.ducmanh.catalogservice.dto.request.ProductUpdateRequest;
import com.ducmanh.catalogservice.dto.response.ProductDetailResponse;
import com.ducmanh.catalogservice.dto.response.ProductListResponse;
import com.ducmanh.catalogservice.entity.Brand;
import com.ducmanh.catalogservice.entity.Category;
import com.ducmanh.catalogservice.entity.Product;
import com.ducmanh.catalogservice.entity.ProductImage;
import com.ducmanh.catalogservice.mapper.ProductMapper;
import com.ducmanh.catalogservice.repository.BrandRepository;
import com.ducmanh.catalogservice.repository.CategoryRepository;
import com.ducmanh.catalogservice.repository.ProductRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CloudinaryService cloudinaryService;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;


    @Transactional(readOnly = true)
    public ProductDetailResponse getProductDetail(String id){
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        return productMapper.toDetailResponse(product);

    }

    public Page<ProductListResponse> getProducts(int page, int size, String keyword, String brandId, String categoryId,
                                                 BigDecimal minPrice, BigDecimal maxPrice){
        Pageable pageable = PageRequest.of(page, size);

        // Tạo bộ lọc Specification động
        Specification<Product> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"));
            }
            if (brandId != null && !brandId.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("brand").get("id"), brandId));
            }
            if (categoryId != null && !categoryId.trim().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
            }
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("basePrice"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("basePrice"), maxPrice));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        // Lấy dữ liệu và map sang dạng List ngắn gọn
        return productRepository.findAll(spec, pageable).map(product -> {
            ProductListResponse response = productMapper.toListResponse(product);
            // Tìm ảnh đại diện (isPrimary = true) để gán vào list
            String primaryImageUrl = product.getImages().stream()
                    .filter(img -> Boolean.TRUE.equals(img.getIsPrimary()))
                    .map(ProductImage::getImageUrl)
                    .findFirst()
                    .orElse(null); // Nếu không có ảnh chính thì để null
            response.setPrimaryImageUrl(primaryImageUrl);
            return response;
        });
    }


    @Transactional
    public ProductDetailResponse createProduct(ProductCreateRequest request){
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found"));
        Brand brand = brandRepository.findById(request.getBrandId()).orElseThrow(() -> new RuntimeException("Brand not found"));

        // Map data và set các quan hệ
        Product product = productMapper.toProduct(request);
        product.setBrand(brand);
        product.setCategory(category);

        product = productRepository.save(product);
        return productMapper.toDetailResponse(product);
    }

    @Transactional
    public ProductDetailResponse updateProduct(String id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));

        productMapper.updateEntityFromRequest(request, product);

        if(!product.getCategory().getId().equals(request.getCategoryId())){
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + request.getCategoryId()));
            product.setCategory(category);
        }

        if(!product.getBrand().getId().equals(request.getBrandId())){
            Brand brand = brandRepository.findById(request.getBrandId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thương hiệu với ID: " + request.getBrandId()));
            product.setBrand(brand);
        }

        product = productRepository.save(product);

        return productMapper.toDetailResponse(product);
    }

    @Transactional
    public void deleteProduct(String id){
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        // BƯỚC QUAN TRỌNG: Xóa ảnh trên Cloudinary trước khi xóa Database
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            for (ProductImage image : product.getImages()) {
                try {
                    String publicId = cloudinaryService.extractPublicId(image.getImageUrl());
                    cloudinaryService.deleteImage(publicId);
                    log.info("Đã xóa ảnh {} trên Cloudinary", publicId);
                } catch (Exception e) {
                    log.error("Lỗi khi xóa ảnh trên Cloudinary: {}", e.getMessage());
                    // Tiếp tục vòng lặp dù lỗi (có thể do ảnh bị xóa thủ công trên mây từ trước)
                }
            }
        }

        // JPA sẽ tự động xóa các dòng trong bảng product_variants và product_images
        // nhờ cấu hình CascadeType.ALL ở Entity
        productRepository.delete(product);
    }


}
