package com.juno.appling.order.domain.repository;

import com.juno.appling.order.domain.entity.DeliveryEntity;
import com.juno.appling.order.domain.model.Delivery;
import com.juno.appling.order.port.DeliveryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DeliveryRepositoryImpl implements DeliveryRepository {
    private final DeliveryJpaRepository deliveryJpaRepository;
    @Override
    public Delivery save(Delivery delivery) {
        return deliveryJpaRepository.save(DeliveryEntity.from(delivery)).toModel();
    }
}
