package com.juno.appling.product.domain.repository;

import com.juno.appling.product.domain.model.Product;

import java.util.List;

public interface ProductRepository {
    List<Product> findAllById(Iterable<Long> ids);

    Product findById(Long productId);

    Product save(Product product);
}
