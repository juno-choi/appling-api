package com.juno.appling.order.domain.entity;

import com.juno.appling.order.domain.model.OrderItem;
import com.juno.appling.order.enums.OrderItemStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Audited
@Table(name = "order_item")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @NotAudited
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_product_id")
    @NotAudited
    private OrderProductEntity orderProduct;

    @Enumerated(EnumType.STRING)
    private OrderItemStatus status;

    private int ea;

    private int productPrice;
    private int productTotalPrice;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static OrderItemEntity from(OrderItem orderItem) {
        if(orderItem == null) {
            return null;
        }
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.id = orderItem.getId();
        orderItemEntity.order = OrderEntity.from(orderItem.getOrder());
        orderItemEntity.orderProduct = OrderProductEntity.from(orderItem.getOrderProduct());
        orderItemEntity.status = orderItem.getStatus();
        orderItemEntity.ea = orderItem.getEa();
        orderItemEntity.productPrice = orderItem.getProductPrice();
        orderItemEntity.productTotalPrice = orderItem.getProductTotalPrice();
        orderItemEntity.createdAt = orderItem.getCreatedAt();
        orderItemEntity.modifiedAt = orderItem.getModifiedAt();
        return orderItemEntity;
    }

    public OrderItem toModel() {
        return OrderItem.builder()
            .id(id)
            .orderProduct(orderProduct.toModel())
            .status(status)
            .ea(ea)
            .productPrice(productPrice)
            .productTotalPrice(productTotalPrice)
            .createdAt(createdAt)
            .modifiedAt(modifiedAt)
            .build();
    }
}
