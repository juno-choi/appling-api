package com.juno.appling.order.domain.repository;

import com.juno.appling.member.domain.entity.MemberEntity;
import com.juno.appling.member.domain.model.Member;
import com.juno.appling.order.domain.entity.OrderEntity;
import com.juno.appling.order.domain.model.Order;
import com.juno.appling.order.enums.OrderStatus;
import com.juno.appling.order.port.OrderCustomJpaRepository;
import com.juno.appling.order.port.OrderJpaRepository;
import com.juno.appling.product.domain.entity.SellerEntity;
import com.juno.appling.product.domain.model.Seller;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
    private final OrderJpaRepository orderJpaRepository;
    private final OrderCustomJpaRepository orderCustomJpaRepository;


    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(OrderEntity.from(order)).toModel();
    }

    @Override
    public Order findById(Long orderId) {
        return orderJpaRepository.findById(orderId).orElseThrow(
            () -> new IllegalArgumentException("유효하지 않은 주문 번호입니다.")
        ).toModel();
    }

    @Override
    public Page<Order> findAllBySeller(Pageable pageable, String search, OrderStatus status, Seller seller) {
        return orderCustomJpaRepository.findAllBySeller(pageable, search, status, SellerEntity.from(seller));
    }

    @Override
    public Page<Order> findAllByMember(Pageable pageable, String search, OrderStatus status, Member member) {
        return orderCustomJpaRepository.findAllByMember(pageable, search, status, MemberEntity.from(member));
    }

    @Override
    public Order findByIdAndSeller(Long orderId, Seller seller) {
        return orderCustomJpaRepository.findByIdAndSeller(orderId, SellerEntity.from(seller)).orElseThrow(
            () -> new IllegalArgumentException("유효하지 않은 주문 번호입니다.")
        );
    }

}
