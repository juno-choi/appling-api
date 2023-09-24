package com.juno.appling.order.application;

import com.juno.appling.global.util.MemberUtil;
import com.juno.appling.order.domain.OrderRepository;
import com.juno.appling.order.domain.OrderItemRepository;
import com.juno.appling.product.domain.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

@ExtendWith({MockitoExtension.class})
class OrderServiceUnitTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MemberUtil memberUtil;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    private MockHttpServletRequest request = new MockHttpServletRequest();

    @Test
    @DisplayName("유효하지 않은 상품 id로 인해 실패")
    void postTempOrderFail1() {
        //given
        //when
        //then
    }

    @Test
    @DisplayName("유효하지 않은 상품이 존재해 실패")
    void postTempOrderFail2() {
        //given
        //when
        //then
    }
    @Test
    @DisplayName("임시 주문 등록에 성공")
    void postTempOrderSuccess() {
        //given
        //when
        //then
    }
}