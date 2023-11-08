package com.juno.appling.order.repository;

import com.juno.appling.global.querydsl.QuerydslConfig;
import com.juno.appling.order.domain.entity.QOrderEntity;
import com.juno.appling.order.domain.entity.QOrderItemEntity;
import com.juno.appling.order.domain.entity.QOrderOptionEntity;
import com.juno.appling.order.domain.entity.QOrderProductEntity;
import com.juno.appling.order.controller.vo.OrderVo;
import com.juno.appling.order.enums.OrderStatus;
import com.juno.appling.product.domain.entity.SellerEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderCustomJpaRepositoryImpl implements OrderCustomJpaRepository {
    private final QuerydslConfig q;

    @Override
    public Page<OrderVo> findAllBySeller(Pageable pageable, String search, OrderStatus status, SellerEntity sellerEntity) {
        QOrderEntity order = QOrderEntity.orderEntity;
        QOrderItemEntity orderItem = QOrderItemEntity.orderItemEntity;
        QOrderProductEntity orderProduct = QOrderProductEntity.orderProductEntity;
        QOrderOptionEntity orderOption = QOrderOptionEntity.orderOptionEntity;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(order.status.eq(OrderStatus.COMPLETE));
        builder.and(orderProduct.seller.id.eq(sellerEntity.getId()));

        List<OrderVo> content = q.query().select(Projections.constructor(OrderVo.class,
                    order
                ))
                .from(order)
                .join(orderItem).on(order.id.eq(orderItem.order.id))
                .join(orderProduct).on(orderProduct.id.eq(orderItem.orderProduct.id))
                .leftJoin(orderOption).on(orderItem.orderOption.id.eq(orderOption.id))
                .where(builder)
                .orderBy(order.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = q.query()
                .from(order)
                .join(orderItem).on(order.id.eq(orderItem.order.id))
                .join(orderProduct).on(orderProduct.id.eq(orderItem.orderProduct.id))
                .leftJoin(orderOption).on(orderItem.orderOption.id.eq(orderOption.id))
                .where(builder).stream().count();

        return new PageImpl<>(content, pageable, total);
    }
}
