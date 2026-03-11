package com.ducmanh.catalogservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageMetadataUpdateRequest {
    private String color;
    private Boolean isPrimary;
}
