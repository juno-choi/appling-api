package com.juno.appling.order.service;

import com.juno.appling.member.controller.request.LoginRequest;
import com.juno.appling.member.controller.response.LoginResponse;
import com.juno.appling.member.infrastruceture.MemberRepository;
import com.juno.appling.member.infrastruceture.SellerRepository;
import com.juno.appling.member.service.MemberAuthService;
import com.juno.appling.order.controller.request.TempOrderDto;
import com.juno.appling.order.controller.request.TempOrderRequest;
import com.juno.appling.order.domain.vo.OrderVo;
import com.juno.appling.order.controller.response.PostTempOrderResponse;
import com.juno.appling.order.domain.Order;
import com.juno.appling.order.domain.OrderItem;
import com.juno.appling.order.infrastructure.DeliveryRepository;
import com.juno.appling.order.infrastructure.OrderItemRepository;
import com.juno.appling.order.infrastructure.OrderRepository;
import com.juno.appling.product.infrastructure.CategoryRepository;
import com.juno.appling.product.infrastructure.OptionRepository;
import com.juno.appling.product.infrastructure.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpHeaders.AUTHORIZATION;
import static com.juno.appling.Base.*;
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
    private OrderRepository orderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    private MockHttpServletRequest request = new MockHttpServletRequest();

    @AfterEach
    void cleanup() {
        deliveryRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        optionRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        sellerRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("임시 주문 성공")
    @Transactional
    void tempOrder() {
        //given
        LoginRequest loginRequest = new LoginRequest(MEMBER_EMAIL, "password");
        LoginResponse login = memberAuthService.login(loginRequest);
        request.addHeader(AUTHORIZATION, "Bearer " + login.getAccessToken());

        List<TempOrderDto> tempOrderDtoList = new ArrayList<>();
        TempOrderDto tempOrderDto1 = TempOrderDto.builder()
            .productId(PRODUCT_ID_APPLE)
            .optionId(PRODUCT_OPTION_ID_APPLE)
            .ea(3)
            .build();
        TempOrderDto tempOrderDto2 =  TempOrderDto.builder()
            .productId(PRODUCT_ID_PEAR)
            .optionId(PRODUCT_OPTION_ID_PEAR)
            .ea(3)
            .build();
        tempOrderDtoList.add(tempOrderDto1);
        tempOrderDtoList.add(tempOrderDto2);

        TempOrderRequest tempOrderRequest = new TempOrderRequest(tempOrderDtoList);

        //when
        PostTempOrderResponse postTempOrderResponse = orderService.postTempOrder(tempOrderRequest, request);

        //then
        Long orderId = postTempOrderResponse.getOrderId();
        Order order = orderRepository.findById(orderId).get();
        List<OrderItem> orderItemList = order.getOrderItemList();
        assertThat(orderItemList).isNotEmpty();
    }

    @Test
    @DisplayName("관리자툴에서 주문 불러오기 성공")
    @Transactional
    void getOrderList() {
        //given
        LoginRequest loginRequest = new LoginRequest(SELLER_EMAIL, "password");
        LoginResponse login = memberAuthService.login(loginRequest);
        request.addHeader(AUTHORIZATION, "Bearer " + login.getAccessToken());
        //when
        Pageable pageable = Pageable.ofSize(10);
        Page<OrderVo> complete = orderService.getOrderListBySeller(pageable, "", "COMPLETE", request);
        //then
        assertThat(complete.getTotalElements()).isGreaterThan(1);
    }
}