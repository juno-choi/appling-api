package com.juno.appling.order.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.member.controller.response.MemberResponse;
import com.juno.appling.order.domain.model.Order;
import com.juno.appling.order.domain.model.OrderItem;
import com.juno.appling.order.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class OrderResponse {
    private Long orderId;
    private MemberResponse member;
    private String orderNumber;
    private List<OrderItemResponse> orderItemList;
    private DeliveryResponse delivery;
    private OrderStatus status;
    private String orderName;
    private int totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .member(order.getMember().toResponseForOthers())
                .orderNumber(order.getOrderNumber())
                .orderItemList(order.getOrderItemList().stream().map(OrderItem::toResponse).collect(Collectors.toList()))
                .delivery(order.getDelivery().toResponse())
                .status(order.getStatus())
                .orderName(order.getOrderName())
                .createdAt(order.getCreatedAt())
                .modifiedAt(order.getModifiedAt())
                .build();
    }
}
