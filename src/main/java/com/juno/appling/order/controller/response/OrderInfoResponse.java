package com.juno.appling.order.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.order.domain.model.Order;
import com.juno.appling.order.domain.model.OrderItem;
import com.juno.appling.order.domain.model.OrderProduct;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class OrderInfoResponse {
    private Long orderId;
    private List<OrderItem> orderItemList;

    public static OrderInfoResponse create(Order order) {
        return OrderInfoResponse.builder()
            .orderId(order.getId())
            .orderItemList(order.getOrderItemList().stream().map(OrderItem::getResponse).toList())
            .build();
    }
}
