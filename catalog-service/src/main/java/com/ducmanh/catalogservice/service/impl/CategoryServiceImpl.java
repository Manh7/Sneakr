package com.ducmanh.catalogservice.service.impl;

import com.ducmanh.catalogservice.dto.request.CategoryRequest;
import com.ducmanh.catalogservice.dto.response.CategoryResponse;
import com.ducmanh.catalogservice.entity.Category;
import com.ducmanh.catalogservice.repository.CategoryRepository;
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


    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .build();
        // Nếu có truyền parentId, tìm Category cha và set vào
        if (request.getParentId() != null && !request.getParentId().isEmpty()) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục cha"));
            category.setParent(parent);
        }

        Category savedCategory = categoryRepository.save(category);
        return mapToResponseTree(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoryTree() {
        // 1. Lấy tất cả danh mục gốc
        List<Category> rootCategories = categoryRepository.findByParentIsNull();

        // 2. Map sang Response và gọi đệ quy để lấy các danh mục con
        return rootCategories.stream()
                .map(this::mapToResponseTree)
                .collect(Collectors.toList());
    }

    // ================= HELPER METHOD: ĐỆ QUY =================
    private CategoryResponse mapToResponseTree(Category category) {
        // Gọi đệ quy để map toàn bộ danh mục con
        List<CategoryResponse> subCategoryResponses = null;
        if (category.getSubCategories() != null && !category.getSubCategories().isEmpty()) {
            subCategoryResponses = category.getSubCategories().stream()
                    .map(this::mapToResponseTree) // Tự gọi lại chính nó
                    .collect(Collectors.toList());
        }

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .subCategories(subCategoryResponses)
                .build();
    }
}
