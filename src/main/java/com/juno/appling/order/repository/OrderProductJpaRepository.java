package com.juno.appling.order.repository;

import com.juno.appling.order.domain.entity.OrderProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductJpaRepository extends JpaRepository<OrderProductEntity, Long> {
}
