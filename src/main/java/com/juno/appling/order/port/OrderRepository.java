package com.juno.appling.order.port;

import com.juno.appling.member.domain.model.Member;
import com.juno.appling.order.domain.model.Order;
import com.juno.appling.order.enums.OrderStatus;
import com.juno.appling.product.domain.model.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepository {
    Order save(Order order);

    Order findById(Long orderId);

    Page<Order> findAll(Pageable pageable, String search, OrderStatus status, Seller seller, Member member);

    Order findByIdAndSeller(Long orderId, Seller seller);

//    OrderVo findByIdAndSeller(Long orderId, Seller seller);
}
