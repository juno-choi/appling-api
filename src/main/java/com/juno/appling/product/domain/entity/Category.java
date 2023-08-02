package com.juno.appling.product.domain.entity;

import com.juno.appling.product.domain.enums.CategoryStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
public class Category {
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

    private Category(@NotNull String name, CategoryStatus status, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.name = name;
        this.status = status;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static Category of(String name, CategoryStatus status){
        LocalDateTime now = LocalDateTime.now();
        return new Category(name, status, now, now);
    }
}
