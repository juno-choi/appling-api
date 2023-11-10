package com.juno.appling.product.domain.model;

import com.juno.appling.order.controller.request.TempOrderDto;
import com.juno.appling.product.enums.OptionStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Option {
    private Long id;
    private String name;
    private int extraPrice;
    private int ea;
    private OptionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public void checkInStock(TempOrderDto tempOrderDto) {
        int ea = tempOrderDto.getEa();
        if (this.ea < ea) {
            throw new IllegalArgumentException(String.format("재고가 부족합니다! 현재 재고 = %s개", this.ea));
        }
    }

    public void checkInStock(int ea) {
        if (this.ea < ea) {
            throw new IllegalArgumentException(String.format("재고가 부족합니다! 현재 재고 = %s개", this.ea));
        }
    }

    public void minusEa(int ea) {
        this.ea -= ea;
    }
}
