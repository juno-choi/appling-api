package com.juno.appling.order.domain.entity;

import com.juno.appling.order.domain.model.OrderItem;
import com.juno.appling.order.enums.OrderItemStatus;
import com.juno.appling.product.enums.ProductType;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_option_id")
    @NotAudited
    private OrderOptionEntity orderOption;

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

        ProductType type = orderItemEntity.orderProduct.getType();
        if(type == ProductType.OPTION) {
            orderItemEntity.orderOption = OrderOptionEntity.from(orderItem.getOrderOption());
        }

        return orderItemEntity;
    }

    public OrderItem toModel() {
        ProductType type = orderProduct.getType();
        if(type == ProductType.OPTION) {
            return OrderItem.builder()
                    .id(id)
                    .orderProduct(orderProduct.toModel())
                    .orderOption(orderOption.toModel())
                    .status(status)
                    .ea(ea)
                    .productPrice(productPrice)
                    .productTotalPrice(productTotalPrice)
                    .createdAt(createdAt)
                    .modifiedAt(modifiedAt)
                    .build();
        }
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
