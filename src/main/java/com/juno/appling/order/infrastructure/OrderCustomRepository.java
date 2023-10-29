package com.juno.appling.order.infrastructure;

import com.juno.appling.global.querydsl.QuerydslConfig;
import com.juno.appling.member.domain.Seller;
import com.juno.appling.order.domain.QOrder;
import com.juno.appling.order.domain.QOrderItem;
import com.juno.appling.order.enums.OrderStatus;
import com.juno.appling.product.controller.response.ProductResponse;
import com.juno.appling.product.domain.Product;
import com.juno.appling.product.domain.QProduct;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderCustomRepository {
    private final QuerydslConfig q;

    public Page<ProductResponse> findAll(Pageable pageable, String search, OrderStatus status, Seller seller) {
        QProduct product = QProduct.product;
        QOrder order = QOrder.order;
        QOrderItem orderItem = QOrderItem.orderItem;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(order.status.eq(OrderStatus.COMPLETE));
        builder.and(product.seller.id.eq(seller.getId()));

        List<Product> fetch = q.query().select(product)
                .from(product)
                .join(orderItem)
                .on(product.id.eq(orderItem.product.id))
                .join(order)
                .on(order.id.eq(orderItem.order.id))
                .where(builder)
                .orderBy(order.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return null;
    }
}
