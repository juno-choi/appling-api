package com.juno.appling.order.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.order.domain.Order;
import com.juno.appling.order.domain.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TempOrderResponse {
    private Long orderId;
    private List<OrderItemVo> orderItemList;


    public static TempOrderResponse of(Order order) {
        List<OrderItem> itemList = order.getOrderItemList();
        List<OrderItemVo> orderItemVoList = new ArrayList<>();
        for(OrderItem o : itemList){
            orderItemVoList.add(OrderItemVo.of(o));
        }

        return new TempOrderResponse(order.getId(), orderItemVoList);
    }
}
