package com.juno.appling.order.domain.repository;

import com.juno.appling.order.domain.model.Order;
import com.juno.appling.order.domain.model.OrderItem;

import java.util.List;

public interface OrderItemRepository {
    OrderItem save(OrderItem orderItem);

    void saveAll(Iterable<OrderItem> orderItemList);

    List<OrderItem> findAllByOrder(Order order);
}
