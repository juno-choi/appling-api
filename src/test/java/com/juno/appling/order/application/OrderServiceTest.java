package com.juno.appling.order.application;

import com.juno.appling.BaseTest;
import com.juno.appling.member.application.MemberAuthService;
import com.juno.appling.member.dto.request.LoginRequest;
import com.juno.appling.member.dto.response.LoginResponse;
import com.juno.appling.order.domain.Order;
import com.juno.appling.order.domain.OrderItem;
import com.juno.appling.order.domain.OrderRepository;
import com.juno.appling.order.domain.OrderSellerList;
import com.juno.appling.order.dto.request.TempOrderDto;
import com.juno.appling.order.dto.request.TempOrderRequest;
import com.juno.appling.order.dto.response.TempOrderResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest
class OrderServiceTest extends BaseTest {
    @Autowired
    private OrderService orderService;

    @Autowired
    private MemberAuthService memberAuthService;

    @Autowired
    private OrderRepository orderRepository;

    private MockHttpServletRequest request = new MockHttpServletRequest();

    @Test
    @DisplayName("임시 주문 성공")
    @Transactional
    void tempOrder() {
        //given
        LoginRequest loginRequest = new LoginRequest(MEMBER_EMAIL, "password");
        LoginResponse login = memberAuthService.login(loginRequest);
        request.addHeader(AUTHORIZATION, "Bearer " + login.getAccessToken());

        List<TempOrderDto> tempOrderDtoList = new ArrayList<>();
        TempOrderDto tempOrderDto1 = new TempOrderDto(PRODUCT1.getId(), 3);
        TempOrderDto tempOrderDto2 = new TempOrderDto(PRODUCT2.getId(), 3);
        tempOrderDtoList.add(tempOrderDto1);
        tempOrderDtoList.add(tempOrderDto2);

        TempOrderRequest tempOrderRequest = new TempOrderRequest(tempOrderDtoList);

        //when
        TempOrderResponse tempOrderResponse = orderService.postTempOrder(tempOrderRequest, request);

        //then
        Long orderId = tempOrderResponse.getOrderId();
        Order order = orderRepository.findById(orderId).get();
        List<OrderItem> orderItemList = order.getOrderItemList();
        List<OrderSellerList> sellerList = order.getSellerList();
        Assertions.assertThat(orderItemList).isNotEmpty();
        Assertions.assertThat(sellerList).isNotEmpty();
    }
}