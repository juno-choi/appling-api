package com.juno.appling.product.port;

import com.juno.appling.product.domain.entity.OptionEntity;
import com.juno.appling.product.domain.entity.ProductEntity;
import com.juno.appling.product.enums.OptionStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OptionJpaRepository extends JpaRepository<OptionEntity, Long> {

    List<OptionEntity> findByProduct(ProductEntity productEntity);

    List<OptionEntity> findByProductAndStatus(ProductEntity productEntity, OptionStatus status);
}
