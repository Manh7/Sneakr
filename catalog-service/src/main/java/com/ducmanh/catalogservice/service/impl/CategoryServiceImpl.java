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

        categoryMapper.updateEntityFromRequest(request, category);

        if(request.getParentId() != null && !request.getParentId().trim().isEmpty()){
            // danh mục không thể làm cha của chính nó
            if (id.equals(request.getParentId())) {
                throw new RuntimeException("Danh mục không thể làm cha của chính nó!");
            }
            Category parentCategory = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục cha"));
            category.setParent(parentCategory);
        }
        else{
            category.setParent(null); // nếu không có parentId thì set parent là null
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
}
