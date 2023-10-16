package com.juno.appling.product.domain;

import com.juno.appling.product.enums.OptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option, Long> {

    List<Option> findByProduct(Product product);

    List<Option> findByProductAndStatus(Product product, OptionStatus status);
}
