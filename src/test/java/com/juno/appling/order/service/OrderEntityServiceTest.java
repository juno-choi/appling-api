package com.juno.appling.order.service;

import static com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpHeaders.AUTHORIZATION;
import static com.juno.appling.Base.MEMBER_LOGIN;
import static com.juno.appling.Base.PRODUCT_ID_APPLE;
import static com.juno.appling.Base.PRODUCT_ID_PEAR;
import static com.juno.appling.Base.PRODUCT_OPTION_ID_APPLE;
import static com.juno.appling.Base.PRODUCT_OPTION_ID_PEAR;
import static com.juno.appling.Base.SELLER_LOGIN;
import static org.assertj.core.api.Assertions.assertThat;

import com.juno.appling.member.repository.MemberJpaRepository;
import com.juno.appling.member.repository.SellerJpaRepository;
import com.juno.appling.member.service.MemberAuthService;
import com.juno.appling.order.controller.request.TempOrderDto;
import com.juno.appling.order.controller.request.TempOrderRequest;
import com.juno.appling.order.controller.response.OrderResponse;
import com.juno.appling.order.controller.response.PostTempOrderResponse;
import com.juno.appling.order.domain.entity.OrderEntity;
import com.juno.appling.order.domain.entity.OrderItemEntity;
import com.juno.appling.order.repository.DeliveryJpaRepository;
import com.juno.appling.order.repository.OrderItemJpaRepository;
import com.juno.appling.order.repository.OrderJpaRepository;
import com.juno.appling.product.repository.CategoryJpaRepository;
import com.juno.appling.product.repository.OptionJpaRepository;
import com.juno.appling.product.repository.ProductJpaRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@SqlGroup({
    @Sql(scripts = {"/sql/init.sql", "/sql/product.sql", "/sql/order.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
})
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
@Transactional(readOnly = true)
@Execution(ExecutionMode.CONCURRENT)
class OrderEntityServiceTest {
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
    @DisplayName("임시 주문 성공")
    @Transactional
    void tempOrder() {
        //given
        request.addHeader(AUTHORIZATION, "Bearer " + MEMBER_LOGIN.getAccessToken());

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
        OrderEntity orderEntity = orderJpaRepository.findById(orderId).get();
        List<OrderItemEntity> orderItemEntityList = orderEntity.getOrderItemList();
        assertThat(orderItemEntityList).isNotEmpty();
    }

    @Test
    @DisplayName("관리자툴에서 주문 불러오기 성공")
    @Transactional
    void getOrderList() {
        //given
        request.addHeader(AUTHORIZATION, "Bearer " + SELLER_LOGIN.getAccessToken());
        //when
        Pageable pageable = Pageable.ofSize(10);
        OrderResponse complete = orderService.getOrderListBySeller(pageable, "", "COMPLETE",
            request);
        //then
        assertThat(complete.getTotalElements()).isGreaterThan(1);
    }
}