package com.juno.appling.order.port;

import com.juno.appling.order.domain.model.Order;

public interface OrderRepository {
    Order save(Order order);

    Order findById(Long orderId);
}
