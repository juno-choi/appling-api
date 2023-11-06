package com.juno.appling.order.domain.entity;

import com.juno.appling.order.domain.model.OrderItem;
import com.juno.appling.order.enums.OrderItemStatus;
import com.juno.appling.product.enums.ProductType;
import com.juno.appling.product.domain.entity.OptionEntity;
import com.juno.appling.product.domain.entity.ProductEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

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
    @JoinColumn(name = "product_id")
    @NotAudited
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id")
    @NotAudited
    private OptionEntity option;

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
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.id = orderItem.getId();
        orderItemEntity.order = OrderEntity.from(orderItem.getOrder());
        orderItemEntity.product = ProductEntity.from(orderItem.getProduct());
        orderItemEntity.option = OptionEntity.from(orderItem.getOption());
        orderItemEntity.orderProduct = OrderProductEntity.from(orderItem.getOrderProduct());
        orderItemEntity.orderOption = OrderOptionEntity.from(orderItem.getOrderOption());
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
            .order(order.toModel())
            .product(product.toModel())
            .option(option.toModel())
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
}
