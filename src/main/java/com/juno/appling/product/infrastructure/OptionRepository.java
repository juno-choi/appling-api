package com.juno.appling.product.infrastructure;

import com.juno.appling.product.domain.Option;
import com.juno.appling.product.domain.Product;
import com.juno.appling.product.enums.OptionStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionRepository extends JpaRepository<Option, Long> {

    List<Option> findByProduct(Product product);

    List<Option> findByProductAndStatus(Product product, OptionStatus status);
}
