package com.juno.appling.order.service;

import com.juno.appling.member.repository.MemberJpaRepository;
import com.juno.appling.member.repository.SellerJpaRepository;
import com.juno.appling.member.service.MemberAuthService;
import com.juno.appling.order.controller.request.CompleteOrderRequest;
import com.juno.appling.order.controller.request.TempOrderDto;
import com.juno.appling.order.controller.request.TempOrderRequest;
import com.juno.appling.order.controller.response.CompleteOrderResponse;
import com.juno.appling.order.controller.response.PostTempOrderResponse;
import com.juno.appling.order.repository.DeliveryJpaRepository;
import com.juno.appling.order.repository.OrderItemJpaRepository;
import com.juno.appling.order.repository.OrderJpaRepository;
import com.juno.appling.product.repository.CategoryJpaRepository;
import com.juno.appling.product.repository.OptionJpaRepository;
import com.juno.appling.product.repository.ProductJpaRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpHeaders.AUTHORIZATION;
import static com.juno.appling.Base.*;
import static com.juno.appling.OrderBase.ORDER_FIRST_ID;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@SqlGroup({
    @Sql(scripts = {"/sql/init.sql", "/sql/product.sql", "/sql/order.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
})
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
@Transactional(readOnly = true)
@Execution(ExecutionMode.CONCURRENT)
class OrderServiceTest {
    @Autowired
    private OrderService orderService;

    @Autowired
    private MemberAuthService memberAuthService;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private SellerJpaRepository sellerJpaRepository;
    @Autowired
    private OptionJpaRepository optionJpaRepository;

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Autowired
    private CategoryJpaRepository categoryJpaRepository;

    @Autowired
    private OrderItemJpaRepository orderItemJpaRepository;

    @Autowired
    private DeliveryJpaRepository deliveryJpaRepository;

    private MockHttpServletRequest request = new MockHttpServletRequest();

    @AfterEach
    void cleanup() {
        deliveryJpaRepository.deleteAll();
        orderItemJpaRepository.deleteAll();
        orderJpaRepository.deleteAll();
        optionJpaRepository.deleteAll();
        productJpaRepository.deleteAll();
        categoryJpaRepository.deleteAll();
        sellerJpaRepository.deleteAll();
        memberJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("임시 주문 등록")
    void tempOrder() {
        //given
        TempOrderRequest tempOrderRequest = TempOrderRequest.builder()
                .orderList(List.of(
                        TempOrderDto.builder()
                                .ea(2)
                                .productId(PRODUCT_ID_NORMAL)
                                .build(),
                        TempOrderDto.builder()
                                .ea(3)
                                .productId(PRODUCT_ID_APPLE)
                                .optionId(PRODUCT_OPTION_ID_APPLE)
                                .build()
                        ))
                .build();

        request.addHeader(AUTHORIZATION, "Bearer " + MEMBER_LOGIN.getAccessToken());
        //when
        PostTempOrderResponse postTempOrderResponse = orderService.postTempOrder(tempOrderRequest, request);
        //then
        assertThat(postTempOrderResponse.getOrderId()).isGreaterThan(0);
    }

    @Test
    @DisplayName("주문 완료")
    void completeOrder() {
        //given
        request.addHeader(AUTHORIZATION, "Bearer " + MEMBER_LOGIN.getAccessToken());
        CompleteOrderRequest completeOrderRequest = CompleteOrderRequest.builder()
                .orderId(ORDER_FIRST_ID)
                .ownerAddress("주소")
                .ownerAddressDetail("상세 주소")
                .ownerName("주문자")
                .ownerTel("01012341234")
                .recipientAddress("수령인 주소")
                .recipientAddressDetail("수령인 상세 주소")
                .recipientName("수령인")
                .recipientTel("01012341234")
                .build();
        //when
        CompleteOrderResponse completeOrderResponse = orderService.completeOrder(completeOrderRequest, request);

        //then
        Assertions.assertThat(completeOrderResponse.getOrderNumber()).isNotNull();
    }

//    @Test
//    @DisplayName("관리자툴에서 주문 불러오기 성공")
//    @Transactional
//    void getOrderList() {
//        //given
//        request.addHeader(AUTHORIZATION, "Bearer " + SELLER_LOGIN.getAccessToken());
//        //when
//        Pageable pageable = Pageable.ofSize(10);
//        OrderResponse complete = orderService.getOrderListBySeller(pageable, "", "COMPLETE",
//            request);
//        //then
//        assertThat(complete.getTotalElements()).isGreaterThan(1);
//    }
}