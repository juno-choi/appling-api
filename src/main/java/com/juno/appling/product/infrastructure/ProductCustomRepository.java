package com.juno.appling.product.infrastructure;

import com.juno.appling.product.controller.response.ProductResponse;
import com.juno.appling.product.domain.Category;
import com.juno.appling.product.enums.ProductStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductCustomRepository {

    Page<ProductResponse> findAll(Pageable pageable, String search, ProductStatus productStatus,
        Category category, Long memberId);

    List<ProductResponse> findAllByIdList(List<Long> productIdList);

}
