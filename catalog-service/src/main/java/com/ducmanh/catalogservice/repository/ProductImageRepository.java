package com.ducmanh.catalogservice.repository;

import com.ducmanh.catalogservice.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, String> {
}
