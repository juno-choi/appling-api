package com.juno.appling.product.domain.model;

import com.juno.appling.product.enums.ProductType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @Test
    @DisplayName("재고가 부족하면 실패")
    void checkInStockFail1(){
        int ea = 10;
        Product product = Product.builder()
                .id(1L)
                .type(ProductType.NORMAL)
                .ea(ea)
                .build();

        assertThatThrownBy(() -> product.checkInStock(ea+1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("재고가 부족")
                .hasMessageContaining(String.valueOf(ea));
    }
    @Test
    @DisplayName("일반 상품 외 상품은 실패")
    void checkInStockFail2(){
        int ea = 100;
        Product product = Product.builder()
                .id(1L)
                .type(ProductType.OPTION)
                .ea(ea)
                .build();
        assertThatThrownBy(() -> product.checkInStock(10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("OPTION");
    }
}