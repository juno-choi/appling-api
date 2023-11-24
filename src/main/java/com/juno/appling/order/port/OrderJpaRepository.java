package com.juno.appling.order.port;

import com.juno.appling.order.domain.entity.OrderEntity;
import com.juno.appling.product.domain.entity.SellerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {
}
