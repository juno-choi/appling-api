package com.juno.appling.order.repository;

import com.juno.appling.member.domain.entity.MemberEntity;
import com.juno.appling.product.domain.entity.SellerEntity;
import com.juno.appling.order.controller.vo.OrderVo;
import com.juno.appling.order.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderCustomJpaRepository {

    Page<OrderVo> findAll(Pageable pageable, String search, OrderStatus status, SellerEntity sellerEntity, MemberEntity memberEntity);

    Optional<OrderVo> findByIdAndSeller(Long orderId, SellerEntity from);
}
