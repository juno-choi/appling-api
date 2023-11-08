package com.juno.appling.order.port;

import com.juno.appling.order.domain.entity.OrderProductEntity;
import com.juno.appling.order.domain.model.OrderProduct;
import com.juno.appling.order.repository.OrderProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderProductRepositoryImpl implements OrderProductRepository {
    private final OrderProductJpaRepository orderProductJpaRepository;

    @Override
    public void saveAll(Iterable<OrderProduct> orderProductList) {
        List<OrderProductEntity> orderProductEntityList = new ArrayList<>();
        for (OrderProduct o : orderProductList) {
            orderProductEntityList.add(OrderProductEntity.from(o));
        }
        orderProductJpaRepository.saveAll(orderProductEntityList).forEach(OrderProductEntity::toModel);
    }

    @Override
    public OrderProduct save(OrderProduct createOrderProduct) {
        return orderProductJpaRepository.save(OrderProductEntity.from(createOrderProduct)).toModel();
    }
}
