package com.juno.appling.product.port;

import com.juno.appling.product.controller.response.ProductResponse;
import com.juno.appling.product.domain.entity.CategoryEntity;
import com.juno.appling.product.enums.ProductStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductCustomJpaRepository {

    Page<ProductResponse> findAll(Pageable pageable, String search, ProductStatus productStatus,
        CategoryEntity categoryEntity, Long memberId);

    List<ProductResponse> findAllByIdList(List<Long> productIdList);

}
