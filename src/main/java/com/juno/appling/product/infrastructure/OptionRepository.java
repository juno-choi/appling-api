package com.juno.appling.product.infrastructure;

import com.juno.appling.product.domain.Option;
import com.juno.appling.product.domain.Product;
import com.juno.appling.product.enums.OptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option, Long> {

    List<Option> findByProduct(Product product);

    List<Option> findByProductAndStatus(Product product, OptionStatus status);
}
