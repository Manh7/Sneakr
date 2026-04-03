package com.ducmanh.catalogservice.service.impl;

import com.ducmanh.catalogservice.dto.request.CategoryRequest;
import com.ducmanh.catalogservice.dto.response.CategoryResponse;
import com.ducmanh.catalogservice.entity.Category;
import com.ducmanh.catalogservice.mapper.CategoryMapper;
import com.ducmanh.catalogservice.repository.CategoryRepository;
import com.ducmanh.catalogservice.repository.ProductRepository;
import com.ducmanh.catalogservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductRepository productRepository;


    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = categoryMapper.toCategory(request);

        if(request.getParentId() != null && !request.getParentId().isEmpty()){
            Category parentCategory = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục cha"));
            category.setParent(parentCategory);
        }

        category = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoryTree() {
        // Lấy tất cả danh mục gốc (không có parent)
        List<Category> rootCategories = categoryRepository.findByParentIsNull();

        // Map từng danh mục gốc sang CategoryResponse, trong đó sẽ đệ quy map cả cây con
        return rootCategories.stream()
                .map(categoryMapper :: toCategoryResponse) // Tự gọi lại chính nó để map toàn bộ cây
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse getCategoryById(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    public CategoryResponse updateCategory(String id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));

        // Cập nhật Tên
        if (request.getName() != null) {
            category.setName(request.getName());
        }

        // Cập nhật Parent
        if (request.getParentId() != null) {
            Category newParent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục cha"));

            // BƯỚC QUAN TRỌNG: CHẶN VÒNG LẶP Ở ĐÂY
            validateNoCyclicDependency(category.getId(), newParent);

            category.setParent(newParent);

        } else if (request.getParentId() == null) {
            // Cần có 1 cờ để biết Admin thực sự muốn gỡ bỏ parent (chuyển thành Root)
            category.setParent(null);
        }

        category = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    public void deleteCategory(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));

        if(category.getSubCategories() != null && !category.getSubCategories().isEmpty()){
            throw new RuntimeException("Không thể xóa danh mục có danh mục con");
        }

//         2. Kiểm tra xem có sản phẩm nào đang thuộc danh mục này không
         if (productRepository.existsByCategoryId(id)) {
             throw new RuntimeException("Không thể xóa danh mục đang có sản phẩm.");
         }

        categoryRepository.delete(category);
    }

//    // ================= HELPER METHOD: ĐỆ QUY =================
//    private CategoryResponse mapToResponseTree(Category category) {
//        // Gọi đệ quy để map toàn bộ danh mục con
//        List<CategoryResponse> subCategoryResponses = null;
//        if (category.getSubCategories() != null && !category.getSubCategories().isEmpty()) {
//            subCategoryResponses = category.getSubCategories().stream()
//                    .map(this::mapToResponseTree) // Tự gọi lại chính nó
//                    .collect(Collectors.toList());
//        }
//
//        return CategoryResponse.builder()
//                .id(category.getId())
//                .name(category.getName())
//                .subCategories(subCategoryResponses)
//                .build();
//    }

    /**
     * Kiểm tra xem newParent có hợp lệ không.
     * Tránh trường hợp:
     * 1. A làm con của chính A.
     * 2. A làm con của C (trong khi C đang là cháu của A).
     */
    private void validateNoCyclicDependency(String currentCategoryId, Category newParent) {
        if (newParent == null) {
            return; // Trở thành danh mục gốc (Root), luôn hợp lệ
        }

        // 1. Không thể tự làm con của chính mình
        if (currentCategoryId.equals(newParent.getId())) {
            throw new RuntimeException("Danh mục không thể làm danh mục con của chính nó.");
        }

        // 2. Lần ngược lên trên gia phả của newParent để xem có đụng trúng currentCategory không
        Category currentAncestor = newParent.getParent();
        while (currentAncestor != null) {
            if (currentCategoryId.equals(currentAncestor.getId())) {
                throw new RuntimeException("Lỗi vòng lặp: Không thể di chuyển danh mục cha vào bên trong danh mục con/cháu của nó.");
            }
            // Tiếp tục bò lên trên
            currentAncestor = currentAncestor.getParent();
        }
    }
}
