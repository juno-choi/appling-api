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

}
