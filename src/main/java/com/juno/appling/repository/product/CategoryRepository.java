package com.juno.appling.repository.product;

import com.juno.appling.domain.entity.product.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
