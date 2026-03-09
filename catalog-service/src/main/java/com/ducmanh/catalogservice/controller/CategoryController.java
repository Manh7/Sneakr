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

    @PostMapping
    public ApiResponse<CategoryResponse> createCategory(@RequestBody @Valid CategoryRequest request){
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.createCategory(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<CategoryResponse>> getCategoryTree(){
        return ApiResponse.<List<CategoryResponse>>builder()
                .result(categoryService.getCategoryTree())
                .build();
    }

}
