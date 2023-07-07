package com.juno.appling.domain.product.entity;

import com.juno.appling.domain.product.enums.CategoryStatus;
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
}
