package com.juno.appling.product.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionRepository extends JpaRepository<Option, Long> {

    List<Option> findByProduct(Product product);
}
