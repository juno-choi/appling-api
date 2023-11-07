package com.juno.appling.product.port;

import com.juno.appling.product.domain.entity.ProductEntity;
import com.juno.appling.product.domain.model.Product;
import com.juno.appling.product.repository.ProductJpaRepository;
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
}
