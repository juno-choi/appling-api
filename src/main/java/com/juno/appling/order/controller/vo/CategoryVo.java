package com.juno.appling.order.controller.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.product.domain.entity.CategoryEntity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Getter
public class CategoryVo {
    @NotNull
    private Long categoryId;
    @NotNull
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static CategoryVo of(CategoryEntity category) {
        return new CategoryVo(category.getId(), category.getName(), category.getCreatedAt(), category.getModifiedAt());
    }
}
