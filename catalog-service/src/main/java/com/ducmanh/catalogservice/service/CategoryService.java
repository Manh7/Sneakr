package com.ducmanh.catalogservice.service;

import com.ducmanh.catalogservice.dto.request.CategoryRequest;
import com.ducmanh.catalogservice.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);
    List<CategoryResponse> getCategoryTree();
    CategoryResponse getCategoryById(String id);
    CategoryResponse updateCategory(String id, CategoryRequest request);
    void deleteCategory(String id);
}
