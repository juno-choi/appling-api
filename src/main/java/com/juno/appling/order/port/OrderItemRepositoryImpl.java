package com.juno.appling.order.port;

import com.juno.appling.order.domain.entity.OrderItemEntity;
import com.juno.appling.order.domain.model.OrderItem;
import com.juno.appling.order.repository.OrderItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderItemRepositoryImpl implements OrderItemRepository {
    private final OrderItemJpaRepository orderItemJpaRepository;


    @Override
    public OrderItem save(OrderItem orderItem) {
        return orderItemJpaRepository.save(OrderItemEntity.from(orderItem)).toModel();
    }
}
