package com.juno.appling.order.infrastructure;

import com.juno.appling.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
