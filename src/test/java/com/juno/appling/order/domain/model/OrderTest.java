package com.juno.appling.order.domain.model;

import com.juno.appling.member.domain.model.Member;
import com.juno.appling.product.domain.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    @Test
    @DisplayName("order create 성공")
    void create() {
        //given
        Member member = Member.builder()
                .build();
        List<Product> productList = List.of(
                Product.builder()
                        .mainTitle("상품1")
                        .build(),
                Product.builder()
                        .mainTitle("상품2")
                        .build(),
                Product.builder()
                        .mainTitle("상품3")
                        .build()
        );
        //when
        Order order = Order.create(member, productList);
        //then
        assertThat(order.getOrderName()).isEqualTo("상품1 외 2개");
    }
}