package com.ducmanh.catalogservice.service;

import com.ducmanh.catalogservice.dto.request.StockUpdateRequest;
import com.ducmanh.catalogservice.dto.request.VariantRequest;
import com.ducmanh.catalogservice.dto.response.ProductVariantResponse;
import com.ducmanh.catalogservice.entity.Product;
import com.ducmanh.catalogservice.entity.ProductVariant;
import com.ducmanh.catalogservice.mapper.ProductVariantMapper;
import com.ducmanh.catalogservice.repository.ProductRepository;
import com.ducmanh.catalogservice.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductVariantService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final ProductVariantMapper productVariantMapper;

    @Transactional
    public ProductVariantResponse createProductVariant(String productId, VariantRequest request) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));

        if(productVariantRepository.existsBySku(request.getSku()))
            throw new RuntimeException("SKU already exists" + request.getSku());

        ProductVariant variant = productVariantMapper.toEntity(request);
        variant.setProduct(product);
        variant.setStockQuantity(0);

        variant = productVariantRepository.save(variant);

        return productVariantMapper.toResponse(variant);
    }

    @Transactional
    public ProductVariantResponse updateVariant(String variantId, VariantRequest request){
        ProductVariant variant = productVariantRepository.findById(variantId).orElseThrow(() -> new RuntimeException("Variant not found"));

        // Nếu Admin đổi mã SKU thành mã mới, cần kiểm tra xem mã mới này đã ai dùng chưa
        if (!variant.getSku().equals(request.getSku()) && productVariantRepository.existsBySku(request.getSku())) {
            throw new RuntimeException("Mã SKU mới đã tồn tại: " + request.getSku());
        }

        productVariantMapper.updateEntityFromRequest(request, variant);

        variant = productVariantRepository.save(variant);

        return productVariantMapper.toResponse(variant);
    }

    @Transactional
    public ProductVariantResponse updateStock(String variantId, StockUpdateRequest request){
        ProductVariant variant = productVariantRepository.findById(variantId).orElseThrow(() -> new RuntimeException("Variant not found"));

        int currentStock = variant.getStockQuantity() != null ? variant.getStockQuantity() : 0;
        int newStock = currentStock + request.getQuantityChange();

        // Chống lỗi nhập kho âm
        if (newStock < 0) {
            throw new RuntimeException("Số lượng tồn kho không đủ để trừ. Tồn kho hiện tại: " + currentStock);
        }

        variant.setStockQuantity(newStock);
        variant = productVariantRepository.save(variant);

        log.info("Đã cập nhật tồn kho cho SKU {}: {} -> {}", variant.getSku(), currentStock, newStock);
        return productVariantMapper.toResponse(variant);
    }

    @Transactional
    public void deleteVariant(String variantId){
        ProductVariant variant = productVariantRepository.findById(variantId).orElseThrow(() -> new RuntimeException("Variant not found"));

        // BẢO VỆ DỮ LIỆU SAGA: Nếu đôi giày này đang bị "giữ chỗ" (nghĩa là có khách đang ở trang
        // thanh toán VNPay nhưng chưa xong), thì tuyệt đối không cho Admin xóa biến thể này.
        if (variant.getReservedStock() != null && variant.getReservedStock() > 0) {
            throw new RuntimeException("Không thể xóa! Biến thể này đang có đơn hàng giữ chỗ chờ thanh toán.");
        }

        productVariantRepository.delete(variant);
    }

    @Transactional(readOnly = true)
    public List<ProductVariantResponse> getVariantsByIds(List<String> variantIds){
        List<ProductVariant> variants = productVariantRepository.findAllById(variantIds);

        return variants.stream()
                .map(productVariantMapper::toResponse)
                .collect(Collectors.toList());
    }

}
