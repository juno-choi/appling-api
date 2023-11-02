package com.juno.appling.product.domain.entity;

import com.juno.appling.product.domain.model.Category;
import com.juno.appling.product.enums.CategoryStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "category")
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @NotNull
    @Column(unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    private CategoryStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static CategoryEntity from(Category category) {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.id = category.getId();
        categoryEntity.name = category.getName();
        categoryEntity.status = category.getStatus();
        categoryEntity.createdAt = category.getCreatedAt();
        categoryEntity.modifiedAt = category.getModifiedAt();
        return categoryEntity;
    }

    public Category toModel() {
        return Category.builder()
            .id(id)
            .name(name)
            .status(status)
            .createdAt(createdAt)
            .modifiedAt(modifiedAt)
            .build();
    }

    private CategoryEntity(@NotNull String name, CategoryStatus status, LocalDateTime createdAt,
        LocalDateTime modifiedAt) {
        this.name = name;
        this.status = status;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static CategoryEntity of(String name, CategoryStatus status) {
        LocalDateTime now = LocalDateTime.now();
        return new CategoryEntity(name, status, now, now);
    }
}
