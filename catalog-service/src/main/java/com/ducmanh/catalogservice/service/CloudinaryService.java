package com.ducmanh.catalogservice.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    /**
     * Upload file ảnh lên Cloudinary
     * @param file File ảnh từ request
     * @return URL của ảnh sau khi upload thành công
     */
    public String uploadImage(MultipartFile file) throws IOException {
        // Tham số thứ 2 là options, ví dụ muốn tạo folder riêng trên Cloudinary:
        // ObjectUtils.asMap("folder", "shoe-store")
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return uploadResult.get("secure_url").toString();
    }

    /**
     * Xóa file ảnh trên Cloudinary dựa vào publicId
     * @param publicId ID của ảnh (thường nằm trong URL)
     */
    public void deleteImage(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    /**
     * Hàm hỗ trợ: Cắt chuỗi URL để lấy publicId dùng cho hàm deleteImage
     * Ví dụ URL: https://res.cloudinary.com/.../image/upload/v1234567/sample.jpg
     * => publicId: "sample"
     */
    public String extractPublicId(String imageUrl) {
        String[] parts = imageUrl.split("/");
        String lastPart = parts[parts.length - 1]; // "sample.jpg"
        return lastPart.substring(0, lastPart.lastIndexOf(".")); // "sample"
    }
}
