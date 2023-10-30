package com.juno.appling.order.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.order.domain.Order;
import com.juno.appling.order.domain.vo.DeliveryVo;
import com.juno.appling.order.domain.vo.MemberVo;
import com.juno.appling.order.domain.vo.OrderItemVo;
import java.time.LocalDateTime;
import lombok.Getter;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderVo {
    private Long orderId;
    private String orderNumber;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private MemberVo member;
    private List<OrderItemVo> orderItemList;
    private DeliveryVo delivery;

    public OrderVo(Order order) {
        this.orderId = order.getId();
        this.orderNumber = order.getOrderNumber();
        this.createdAt = order.getCreatedAt();
        this.modifiedAt = order.getModifiedAt();
        this.member = MemberVo.fromMemberEntity(order.getMember());
        this.orderItemList = order.getOrderItemList().stream().map(OrderItemVo::fromOrderItemEntity).toList();
        this.delivery = order.getDeliveryList().stream().map(DeliveryVo::fromDeliveryEntity).toList().size() > 0 ? order.getDeliveryList().stream().map(DeliveryVo::fromDeliveryEntity).toList().get(0) : null;
    }
}
