package com.juno.appling.order.domain.model;

import com.juno.appling.product.domain.model.Option;
import com.juno.appling.product.enums.OptionStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderOption {
    private Long id;
    private OrderProduct orderProduct;
    private String name;
    private int extraPrice;
    private int ea;
    private OptionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static OrderOption create(Option option) {
        return OrderOption.builder()
            .id(option.getId())
            .name(option.getName())
            .extraPrice(option.getExtraPrice())
            .ea(option.getEa())
            .status(option.getStatus())
            .createdAt(option.getCreatedAt())
            .modifiedAt(option.getModifiedAt())
            .build();
    }
}
