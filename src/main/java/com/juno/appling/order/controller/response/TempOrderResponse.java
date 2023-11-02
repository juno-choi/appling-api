package com.juno.appling.order.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.order.domain.entity.OrderEntity;
import com.juno.appling.order.domain.entity.OrderItemEntity;
import com.juno.appling.order.domain.vo.OrderItemVo;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class TempOrderResponse {
    private Long orderId;
    private List<OrderItemVo> orderItemList;


    public static TempOrderResponse from(OrderEntity orderEntity) {
        List<OrderItemEntity> itemList = orderEntity.getOrderItemList();
        List<OrderItemVo> orderItemVoList = new ArrayList<>();
        for(OrderItemEntity o : itemList){
            orderItemVoList.add(OrderItemVo.from(o));
        }

        return TempOrderResponse.builder()
            .orderId(orderEntity.getId())
            .orderItemList(orderItemVoList)
            .build();
    }
}
