package com.juno.appling.product.domain.repository;

import com.juno.appling.product.domain.model.Option;

public interface OptionRepository {
    Option save(Option option);
    Option findById(Long id);
}
