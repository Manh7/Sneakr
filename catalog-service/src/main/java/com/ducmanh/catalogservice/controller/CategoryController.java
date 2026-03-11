package com.ducmanh.catalogservice.controller;

import com.ducmanh.catalogservice.dto.ApiResponse;
import com.ducmanh.catalogservice.dto.request.CategoryRequest;
import com.ducmanh.catalogservice.dto.response.CategoryResponse;
import com.ducmanh.catalogservice.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    // For admin

    @PostMapping
    public ApiResponse<CategoryResponse> createCategory(@RequestBody @Valid CategoryRequest request){
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.createCategory(request))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<CategoryResponse> updateCategory(@PathVariable String id, @RequestBody @Valid CategoryRequest request){
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.updateCategory(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCategory(@PathVariable String id){
        categoryService.deleteCategory(id);
        return ApiResponse.<String>builder()
                .result("Category deleted successfully")
                .build();
    }

    // For user

    @GetMapping
    public ApiResponse<List<CategoryResponse>> getCategoryTree(){
        return ApiResponse.<List<CategoryResponse>>builder()
                .result(categoryService.getCategoryTree())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> getCategoryById(@PathVariable String id){
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.getCategoryById(id))
                .build();
    }



}
