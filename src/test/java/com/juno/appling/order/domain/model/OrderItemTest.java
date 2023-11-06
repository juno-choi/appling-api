package com.juno.appling.order.domain.model;

import com.juno.appling.member.domain.model.Member;
import com.juno.appling.member.enums.MemberRole;
import com.juno.appling.order.controller.request.TempOrderDto;
import com.juno.appling.order.enums.OrderStatus;
import com.juno.appling.product.domain.model.Option;
import com.juno.appling.product.domain.model.Product;
import com.juno.appling.product.enums.OptionStatus;
import com.juno.appling.product.enums.ProductStatus;
import com.juno.appling.product.enums.ProductType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.juno.appling.Base.PRODUCT_ID_APPLE;
import static org.assertj.core.api.Assertions.assertThat;

class OrderItemTest {

    @Test
    @DisplayName("Product와 Order로 일반 상품의 OrderItem을 생성한다.")
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

        Product product = Product.builder()
            .id(1L)
            .price(10000)
            .ea(10)
            .status(ProductStatus.NORMAL)
            .type(ProductType.NORMAL)
            .mainTitle("상품명1")
            .mainExplanation("상품 설명1")
            .productMainExplanation("상품 제목1")
            .productSubExplanation("상품 설명1")
            .mainImage("mainImage")
            .image1("image1")
            .image2("image2")
            .image3("image3")
            .viewCnt(0L)
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .build();

        TempOrderDto tempOrderDto = TempOrderDto.builder()
            .productId(PRODUCT_ID_APPLE)
            .ea(5)
            .build();

        //when
        OrderItem orderItem = OrderItem.create(order, product, tempOrderDto);
        
        //then
        assertThat(orderItem).isNotNull();
    }

    @Test
    @DisplayName("Product와 Order로 옵션 상품의 OrderItem을 생성한다.")
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

        List<Option> optionList = List.of(
                Option.builder()
                        .id(1L)
                        .name("option1")
                        .extraPrice(1000)
                        .ea(10)
                        .status(OptionStatus.NORMAL)
                        .createdAt(LocalDateTime.now())
                        .modifiedAt(LocalDateTime.now())
                        .build(),
                Option.builder()
                        .id(2L)
                        .name("option2")
                        .extraPrice(2000)
                        .ea(10)
                        .status(OptionStatus.NORMAL)
                        .createdAt(LocalDateTime.now())
                        .modifiedAt(LocalDateTime.now())
                        .build()
        );

        Product product = Product.builder()
                .id(1L)
                .price(10000)
                .ea(10)
                .status(ProductStatus.NORMAL)
                .type(ProductType.OPTION)
                .mainTitle("상품명1")
                .mainExplanation("상품 설명1")
                .productMainExplanation("상품 제목1")
                .productSubExplanation("상품 설명1")
                .mainImage("mainImage")
                .image1("image1")
                .image2("image2")
                .image3("image3")
                .viewCnt(0L)
                .optionList(optionList)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        TempOrderDto tempOrderDto = TempOrderDto.builder()
                .productId(PRODUCT_ID_APPLE)
                .optionId(1L)
                .ea(5)
                .build();

        //when
        OrderItem orderItem = OrderItem.create(order, product,  tempOrderDto);

        //then
        assertThat(orderItem.getProductPrice()).isEqualTo(
                product.getPrice() +
                        optionList.stream().filter(o -> o.getId().equals(tempOrderDto.getOptionId())).findFirst().get().getExtraPrice()
        );
    }
}