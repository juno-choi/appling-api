package com.juno.appling.product.domain.repository;

import com.juno.appling.product.domain.entity.ProductEntity;
import com.juno.appling.product.domain.model.Product;
import com.juno.appling.product.port.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    private final ProductJpaRepository productJpaRepository;
    @Override
    public List<Product> findAllById(Iterable<Long> ids) {
        return productJpaRepository.findAllById(ids).stream()
                .map(ProductEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Product findById(Long productId) {
        ProductEntity productEntity = productJpaRepository.findById(productId).orElseThrow(
            () -> new IllegalArgumentException("유효하지 않은 상품입니다.")
        );
        return productEntity.toModel();
    }

    @Override
    public Product save(Product product) {
        return productJpaRepository.save(ProductEntity.from(product)).toModel();
    }
}
