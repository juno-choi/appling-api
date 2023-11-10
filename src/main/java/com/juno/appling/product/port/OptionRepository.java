package com.juno.appling.product.port;

import com.juno.appling.product.domain.model.Option;

public interface OptionRepository {
    Option save(Option option);
    Option findById(Long id);
}
