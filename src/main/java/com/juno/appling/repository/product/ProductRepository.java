package com.juno.appling.repository.product;

import com.juno.appling.domain.entity.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
