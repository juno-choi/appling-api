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
    private Long optionId;
    private String name;
    private int extraPrice;
    @Enumerated(EnumType.STRING)
    private OptionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static OrderOptionEntity from(OrderOption orderOption) {
        if(orderOption == null) {
            return null;
        }
        OrderOptionEntity orderOptionEntity = new OrderOptionEntity();
        orderOptionEntity.id = orderOption.getId();
        orderOptionEntity.optionId = orderOption.getOptionId();
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
            .optionId(optionId)
            .name(name)
            .extraPrice(extraPrice)
            .status(status)
            .createdAt(createdAt)
            .modifiedAt(modifiedAt)
            .build();
    }

}
