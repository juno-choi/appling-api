package com.juno.appling.order.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.order.domain.entity.OrderItemEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ReceiptVo {
    private SellerVo seller;
    private String productName;
    private int ea;
    private int totalPrice;

    public static ReceiptVo of(OrderItemEntity orderItemEntity) {
        return new ReceiptVo(SellerVo.of(orderItemEntity.getProduct().getSeller()), orderItemEntity.getProduct().getMainTitle(),
            orderItemEntity.getEa(), orderItemEntity.getProductTotalPrice());
    }
}
