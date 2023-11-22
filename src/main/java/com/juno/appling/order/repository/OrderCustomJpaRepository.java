package com.juno.appling.order.repository;

import com.juno.appling.member.domain.entity.MemberEntity;
import com.juno.appling.order.domain.model.Order;
import com.juno.appling.order.enums.OrderStatus;
import com.juno.appling.product.domain.entity.SellerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderCustomJpaRepository {

    Page<Order> findAll(Pageable pageable, String search, OrderStatus status, SellerEntity sellerEntity, MemberEntity memberEntity);

//    Optional<OrderVo> findByIdAndSeller(Long orderId, SellerEntity from);
}
