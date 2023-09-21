package com.juno.appling.order.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.order.domain.OrdersDetail;
import com.juno.appling.product.domain.Product;
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

    public static ReceiptVo of(OrdersDetail ordersDetail) {
        return new ReceiptVo(SellerVo.of(ordersDetail.getProduct().getSeller()), ordersDetail.getProduct().getMainTitle(),
            ordersDetail.getEa(), ordersDetail.getProductTotalPrice());
    }
}
