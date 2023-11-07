package com.juno.appling.order.domain.model;

import com.juno.appling.member.domain.model.Member;
import com.juno.appling.member.enums.MemberRole;
import com.juno.appling.order.enums.OrderStatus;
import com.juno.appling.product.enums.ProductType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class OrderItemTest {

    @Test
    @DisplayName("일반 상품의 OrderItem을 생성 성공.")
    void createNormal(){
        //given
        Order order = Order.builder()
            .id(1L)
            .status(OrderStatus.TEMP)
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .member(Member.builder()
                .id(1L)
                .nickname("nickname")
                .name("name")
                .email("email")
                .password("password")
                .role(MemberRole.MEMBER)
                .build())
            .orderName("주문번호")
            .build();

        OrderProduct orderProduct = OrderProduct.builder()
                .type(ProductType.NORMAL)
                .price(1000)
                .ea(10)
                .build();

        int ea = 2;

        //when
        OrderItem orderItem = OrderItem.create(order, orderProduct, null, ea);
        
        //then
        assertThat(orderItem.getProductTotalPrice()).isEqualTo(orderProduct.getPrice() * ea);
    }

    @Test
    @DisplayName("옵션 상품의 OrderItem을 생성 성공")
    void createOption(){
        //given
        Order order = Order.builder()
                .id(1L)
                .status(OrderStatus.TEMP)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .member(Member.builder()
                        .id(1L)
                        .nickname("nickname")
                        .name("name")
                        .email("email")
                        .password("password")
                        .role(MemberRole.MEMBER)
                        .build())
                .orderName("주문번호")
                .build();

        OrderProduct orderProduct = OrderProduct.builder()
                .type(ProductType.OPTION)
                .price(1000)
                .ea(0)
                .build();

        OrderOption orderOption = OrderOption.builder()
                .extraPrice(100)
                .ea(10)
                .build();

        int ea = 3;

        //when
        OrderItem orderItem = OrderItem.create(order, orderProduct, orderOption, ea);

        //then
        assertThat(orderItem.getProductTotalPrice()).isEqualTo((orderProduct.getPrice() + orderOption.getExtraPrice()) * ea);
    }
}