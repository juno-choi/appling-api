package com.juno.appling.order.controller.response;

import static org.junit.jupiter.api.Assertions.*;

import com.juno.appling.order.domain.model.Order;
import com.juno.appling.order.domain.model.OrderItem;
import com.juno.appling.order.domain.model.OrderProduct;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderInfoResponseTest {

    @Test
    @DisplayName("OrderInfoResponse create에 성공")
    void create() {
        //given
        Order order = Order.builder()
            .id(1L)
            .orderItemList(List.of(
                OrderItem.builder()
                    .id(1L)
                    .orderProduct(OrderProduct.builder()
                        .id(2L)
                        .mainTitle("상품2")
                        .build())
                    .build(),
                OrderItem.builder()
                    .id(2L)
                    .orderProduct(OrderProduct.builder()
                        .id(1L)
                        .mainTitle("상품1")
                        .build())
                    .build()
            ))
            .build();
        //when
        OrderInfoResponse orderInfoResponse = OrderInfoResponse.create(order);
        //then
        Assertions.assertThat(orderInfoResponse.getOrderItemList().get(0).getOrderProduct().getId()).isEqualTo(2L);
    }
}