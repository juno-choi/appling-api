package com.juno.appling.order.domain.entity;

import com.juno.appling.order.domain.model.OrderOption;
import com.juno.appling.product.enums.OptionStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Audited
@Table(name = "order_option")
public class OrderOptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_option_id")
    private Long id;

    private String name;
    private int extraPrice;
    @Enumerated(EnumType.STRING)
    private OptionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static OrderOptionEntity from(OrderOption orderOption) {
        OrderOptionEntity orderOptionEntity = new OrderOptionEntity();
        orderOptionEntity.id = orderOption.getId();
        orderOptionEntity.name = orderOption.getName();
        orderOptionEntity.extraPrice = orderOption.getExtraPrice();
        orderOptionEntity.status = orderOption.getStatus();
        orderOptionEntity.createdAt = orderOption.getCreatedAt();
        orderOptionEntity.modifiedAt = orderOption.getModifiedAt();
        return orderOptionEntity;
    }

    public OrderOption toModel(){
        return OrderOption.builder()
            .id(id)
            .name(name)
            .extraPrice(extraPrice)
            .status(status)
            .createdAt(createdAt)
            .modifiedAt(modifiedAt)
            .build();
    }

}
