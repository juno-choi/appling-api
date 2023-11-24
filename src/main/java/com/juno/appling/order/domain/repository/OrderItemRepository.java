package com.juno.appling.order.domain.repository;

import com.juno.appling.order.domain.model.OrderItem;

public interface OrderItemRepository {
    OrderItem save(OrderItem orderItem);

    void saveAll(Iterable<OrderItem> orderItemList);
}
