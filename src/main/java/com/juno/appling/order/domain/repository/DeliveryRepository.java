package com.juno.appling.order.domain.repository;

import com.juno.appling.order.domain.model.Delivery;

public interface DeliveryRepository {
    Delivery save(Delivery delivery);
}
