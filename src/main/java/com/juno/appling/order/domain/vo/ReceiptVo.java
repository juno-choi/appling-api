package com.juno.appling.order.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.order.domain.OrderItem;
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

    public static ReceiptVo of(OrderItem orderItem) {
        return new ReceiptVo(SellerVo.of(orderItem.getProduct().getSeller()), orderItem.getProduct().getMainTitle(),
            orderItem.getEa(), orderItem.getProductTotalPrice());
    }
}
