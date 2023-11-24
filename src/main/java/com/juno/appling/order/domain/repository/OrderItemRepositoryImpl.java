package com.juno.appling.order.domain.repository;

import com.juno.appling.order.domain.entity.OrderItemEntity;
import com.juno.appling.order.domain.model.OrderItem;
import com.juno.appling.order.port.OrderItemJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderItemRepositoryImpl implements OrderItemRepository {
    private final OrderItemJpaRepository orderItemJpaRepository;


    @Override
    public OrderItem save(OrderItem orderItem) {
        return orderItemJpaRepository.save(OrderItemEntity.from(orderItem)).toModel();
    }

    @Override
    public void saveAll(Iterable<OrderItem> orderItemList) {
        List<OrderItemEntity> orderItemEntityList = new ArrayList<>();
        for (OrderItem o : orderItemList) {
            orderItemEntityList.add(OrderItemEntity.from(o));
        }
        orderItemJpaRepository.saveAll(orderItemEntityList).forEach(OrderItemEntity::toModel);
    }
}
