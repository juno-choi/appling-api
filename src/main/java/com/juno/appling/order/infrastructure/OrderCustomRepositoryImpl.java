package com.juno.appling.order.infrastructure;

import com.juno.appling.global.querydsl.QuerydslConfig;
import com.juno.appling.member.domain.Seller;
import com.juno.appling.order.domain.vo.OrderVo;
import com.juno.appling.order.domain.QOrder;
import com.juno.appling.order.domain.QOrderItem;
import com.juno.appling.order.enums.OrderStatus;
import com.juno.appling.product.domain.QOption;
import com.juno.appling.product.domain.QProduct;
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
public class OrderCustomRepositoryImpl implements OrderCustomRepository {
    private final QuerydslConfig q;

    @Override
    public Page<OrderVo> findAllBySeller(Pageable pageable, String search, OrderStatus status, Seller seller) {
        QProduct product = QProduct.product;
        QOrder order = QOrder.order;
        QOrderItem orderItem = QOrderItem.orderItem;
        QOption option = QOption.option;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(order.status.eq(OrderStatus.COMPLETE));
        builder.and(product.seller.id.eq(seller.getId()));

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
