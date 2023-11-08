package com.juno.appling.product.domain.model;

import com.juno.appling.product.controller.response.CategoryResponse;
import com.juno.appling.product.enums.CategoryStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Category {
    private Long id;
    private String name;
    private CategoryStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public CategoryResponse toResponse() {
        return CategoryResponse.builder()
            .categoryId(id)
            .name(name)
            .status(status)
            .createdAt(createdAt)
            .modifiedAt(modifiedAt)
            .build();
    }
}
