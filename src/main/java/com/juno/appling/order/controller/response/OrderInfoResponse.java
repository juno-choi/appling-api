package com.juno.appling.order.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.order.domain.model.Order;
import com.juno.appling.order.domain.model.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class OrderInfoResponse {
    private Long orderId;
    private List<OrderItemResponse> orderItemList;

    public static OrderInfoResponse create(Order order) {
        return OrderInfoResponse.builder()
            .orderId(order.getId())
            .orderItemList(order.getOrderItemList().stream().map(OrderItem::toResponse).toList())
            .build();
    }
}
