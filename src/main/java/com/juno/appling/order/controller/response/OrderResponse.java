package com.juno.appling.order.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.member.controller.response.MemberResponse;
import com.juno.appling.order.domain.Order;
import com.juno.appling.order.domain.vo.OrderItemVo;
import lombok.Getter;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderResponse {
    private Long orderId;
    private String orderNumber;
    private MemberResponse memberResponse;
    private List<OrderItemVo> orderItemList;

    public OrderResponse(Order order) {
        this.orderId = order.getId();
        this.orderNumber = order.getOrderNumber();
        this.orderItemList = order.getOrderItemList().stream().map(OrderItemVo::of).toList();
    }
}
