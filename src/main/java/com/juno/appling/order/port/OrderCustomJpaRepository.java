package com.juno.appling.order.port;

import com.juno.appling.member.domain.entity.MemberEntity;
import com.juno.appling.order.domain.model.Order;
import com.juno.appling.order.enums.OrderStatus;
import com.juno.appling.product.domain.entity.SellerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderCustomJpaRepository {

    Page<Order> findAllBySeller(Pageable pageable, String search, OrderStatus status, SellerEntity sellerEntity);
    Page<Order> findAllByMember(Pageable pageable, String search, OrderStatus status, MemberEntity memberEntity);

    Optional<Order> findByIdAndSeller(Long orderId, SellerEntity from);
}
