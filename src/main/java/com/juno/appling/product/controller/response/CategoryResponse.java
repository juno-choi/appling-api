package com.juno.appling.product.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.product.domain.Product;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Getter
@Builder
public class CategoryResponse {
    @NotNull
    private Long categoryId;
    @NotNull
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static CategoryResponse from(Product product) {
        return CategoryResponse.builder()
            .categoryId(product.getCategory().getId())
            .name(product.getCategory().getName())
            .createdAt(product.getCategory().getCreatedAt())
            .modifiedAt(product.getCategory().getModifiedAt())
            .build();
    }
}
