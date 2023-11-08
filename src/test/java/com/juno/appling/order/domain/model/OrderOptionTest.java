package com.juno.appling.order.domain.model;

import com.juno.appling.product.domain.model.Option;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderOptionTest {
    @Test
    @DisplayName("option id가 없으면 실패")
    void createFail1() {
        //given
        List<Option> optionList = List.of(
            Option.builder()
                .id(1L)
                .name("option1")
                .build(),
            Option.builder()
                .id(2L)
                .name("option2")
                .build()
        );
        //when
        //then
        Assertions.assertThatThrownBy(() -> OrderOption.create(optionList, 3L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("option id = 3")
        ;
    }

    @Test
    @DisplayName("OrderOption create 성공")
    void create() {
        //given
        Long targetId = 1L;

        List<Option> optionList = List.of(
                Option.builder()
                        .id(targetId)
                        .name("option1")
                        .build(),
                Option.builder()
                        .id(2L)
                        .name("option2")
                        .build()
        );
        //when
        OrderOption orderOption = OrderOption.create(optionList, targetId);
        //then
        Assertions.assertThat(orderOption.getId()).isEqualTo(targetId);
    }
}