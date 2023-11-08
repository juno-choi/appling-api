package com.juno.appling.order.controller.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.product.domain.entity.CategoryEntity;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

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

    public static CategoryVo of(CategoryEntity categoryEntity) {
        return new CategoryVo(categoryEntity.getId(), categoryEntity.getName(), categoryEntity.getCreatedAt(), categoryEntity.getModifiedAt());
    }
}
