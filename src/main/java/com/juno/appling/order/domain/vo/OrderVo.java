package com.juno.appling.order.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.order.domain.entity.OrderEntity;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

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

    public OrderVo(OrderEntity orderEntity) {
        this.orderId = orderEntity.getId();
        this.orderNumber = orderEntity.getOrderNumber();
        this.createdAt = orderEntity.getCreatedAt();
        this.modifiedAt = orderEntity.getModifiedAt();
        this.member = MemberVo.fromMemberEntity(orderEntity.getMember());
        this.orderItemList = orderEntity.getOrderItemList().stream().map(OrderItemVo::from).toList();
        this.delivery = orderEntity.getDeliveryList().stream().map(DeliveryVo::fromDeliveryEntity).toList().size() > 0 ? orderEntity.getDeliveryList().stream().map(DeliveryVo::fromDeliveryEntity).toList().get(0) : null;
    }
}
