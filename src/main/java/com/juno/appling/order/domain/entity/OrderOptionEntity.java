package com.juno.appling.order.domain.entity;

import com.juno.appling.order.domain.model.OrderOption;
import com.juno.appling.product.enums.OptionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;

public class OrderOptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_option_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_product_id")
    private OrderProductEntity orderProduct;
    private String name;
    private int extraPrice;
    private int ea;
    @Enumerated(EnumType.STRING)
    private OptionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static OrderOptionEntity from(OrderOption orderOption) {
        OrderOptionEntity orderOptionEntity = new OrderOptionEntity();
        orderOptionEntity.id = orderOption.getId();
        orderOptionEntity.orderProduct = OrderProductEntity.from(orderOption.getOrderProduct());
        orderOptionEntity.name = orderOption.getName();
        orderOptionEntity.extraPrice = orderOption.getExtraPrice();
        orderOptionEntity.ea = orderOption.getEa();
        orderOptionEntity.status = orderOption.getStatus();
        orderOptionEntity.createdAt = orderOption.getCreatedAt();
        orderOptionEntity.modifiedAt = orderOption.getModifiedAt();
        return orderOptionEntity;
    }

    public OrderOption toModel(){
        return OrderOption.builder()
            .id(id)
            .orderProduct(orderProduct.toModel())
            .name(name)
            .extraPrice(extraPrice)
            .ea(ea)
            .status(status)
            .createdAt(createdAt)
            .modifiedAt(modifiedAt)
            .build();
    }

}
