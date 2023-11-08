package com.juno.appling.order.port;

import com.juno.appling.order.domain.model.OrderProduct;

public interface OrderProductRepository {
    void saveAll(Iterable<OrderProduct> orderProductList);

    OrderProduct save(OrderProduct createOrderProduct);
}
