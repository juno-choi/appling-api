package com.juno.appling.order.repository;

import com.juno.appling.global.querydsl.QuerydslConfig;
import com.juno.appling.member.domain.entity.SellerEntity;
import com.juno.appling.order.domain.entity.QOrderEntity;
import com.juno.appling.order.domain.entity.QOrderItemEntity;
import com.juno.appling.order.domain.vo.OrderVo;
import com.juno.appling.order.enums.OrderStatus;
import com.juno.appling.product.domain.entity.QOptionEntity;
import com.juno.appling.product.domain.entity.QProductEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderCustomJpaRepositoryImpl implements OrderCustomJpaRepository {
    private final QuerydslConfig q;

    @Override
    public Page<OrderVo> findAllBySeller(Pageable pageable, String search, OrderStatus status, SellerEntity sellerEntity) {
        QProductEntity product = QProductEntity.productEntity;
        QOrderEntity order = QOrderEntity.orderEntity;
        QOrderItemEntity orderItem = QOrderItemEntity.orderItemEntity;
        QOptionEntity option = QOptionEntity.optionEntity;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(order.status.eq(OrderStatus.COMPLETE));
        builder.and(product.seller.id.eq(sellerEntity.getId()));

        List<OrderVo> content = q.query().select(Projections.constructor(OrderVo.class,
                    order
                ))
                .from(order)
                .join(orderItem)
                .on(order.id.eq(orderItem.order.id)).join(product)
                .on(product.id.eq(orderItem.product.id)).leftJoin(option)
                .on(orderItem.option.id.eq(option.id)).where(builder)
                .orderBy(order.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = q.query()
                .from(order)
                .join(orderItem)
                .on(order.id.eq(orderItem.order.id))
                .join(product)
                .on(product.id.eq(orderItem.product.id))
                .leftJoin(option)
                .on(orderItem.option.id.eq(option.id))
                .where(builder).stream().count();

        return new PageImpl<>(content, pageable, total);
    }
}
