package com.juno.appling.order.repository;

import com.juno.appling.global.querydsl.QuerydslConfig;
import com.juno.appling.member.domain.entity.MemberEntity;
import com.juno.appling.order.domain.entity.*;
import com.juno.appling.order.domain.model.Order;
import com.juno.appling.order.enums.OrderStatus;
import com.juno.appling.product.domain.entity.SellerEntity;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderCustomJpaRepositoryImpl implements OrderCustomJpaRepository {
    private final QuerydslConfig q;

    @Override
    public Page<Order> findAll(Pageable pageable, String search, OrderStatus status, SellerEntity sellerEntity, MemberEntity memberEntity) {
        QOrderEntity order = QOrderEntity.orderEntity;
        QOrderItemEntity orderItem = QOrderItemEntity.orderItemEntity;
        QOrderProductEntity orderProduct = QOrderProductEntity.orderProductEntity;
        QOrderOptionEntity orderOption = QOrderOptionEntity.orderOptionEntity;
        QDeliveryEntity delivery = QDeliveryEntity.deliveryEntity;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(order.status.eq(OrderStatus.COMPLETE));
        if(sellerEntity != null) {
            builder.and(orderProduct.seller.id.eq(sellerEntity.getId()));
        }
        if(memberEntity != null) {
            builder.and(order.member.id.eq(memberEntity.getId()));
        }

        if(search != null && !search.isBlank()) {
            builder.and(orderProduct.mainTitle.contains(search));
        }

        List<OrderEntity> fetch = q.query().selectFrom(order)
                .join(orderItem).on(order.id.eq(orderItem.order.id)).fetchJoin()
                .join(orderProduct).on(orderProduct.id.eq(orderItem.orderProduct.id)).fetchJoin()
                .leftJoin(orderOption).on(orderOption.id.eq(orderItem.orderProduct.orderOption.id)).fetchJoin()
                .leftJoin(delivery).on(order.delivery.id.eq(delivery.id)).fetchJoin()
                .where(builder)
                .orderBy(order.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Order> content = fetch.stream().map(OrderEntity::toModel).collect(Collectors.toList());

        Long total = q.query()
                .from(order)
                .join(orderItem).on(order.id.eq(orderItem.order.id)).fetchJoin()
                .join(orderProduct).on(orderProduct.id.eq(orderItem.orderProduct.id)).fetchJoin()
                .leftJoin(orderOption).on(orderOption.id.eq(orderItem.orderProduct.orderOption.id)).fetchJoin()
                .leftJoin(delivery).on(order.delivery.id.eq(delivery.id)).fetchJoin()
                .where(builder).stream().count();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Optional<Order> findByIdAndSeller(Long orderId, SellerEntity sellerEntity) {
        QOrderEntity order = QOrderEntity.orderEntity;
        QOrderItemEntity orderItem = QOrderItemEntity.orderItemEntity;
        QOrderProductEntity orderProduct = QOrderProductEntity.orderProductEntity;
        QOrderOptionEntity orderOption = QOrderOptionEntity.orderOptionEntity;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(order.id.eq(orderId));
        builder.and(orderProduct.seller.id.eq(sellerEntity.getId()));

        OrderEntity fetch = q.query().selectFrom(order)
                .join(orderItem).on(order.id.eq(orderItem.order.id)).fetchJoin()
                .join(orderProduct).on(orderProduct.id.eq(orderItem.orderProduct.id)).fetchJoin()
                .leftJoin(orderOption).on(orderProduct.orderOption.id.eq(orderOption.id)).fetchJoin()
                .where(builder)
                .fetchFirst();

        return Optional.ofNullable(fetch.toModel());
    }
}
