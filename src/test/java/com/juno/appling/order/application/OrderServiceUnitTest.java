package com.juno.appling.order.application;

import com.juno.appling.global.util.MemberUtil;
import com.juno.appling.order.domain.*;
import com.juno.appling.order.dto.request.TempOrderDto;
import com.juno.appling.order.dto.request.TempOrderRequest;
import com.juno.appling.order.dto.response.TempOrderResponse;
import com.juno.appling.product.domain.Product;
import com.juno.appling.product.domain.ProductRepository;
import com.juno.appling.product.enums.Status;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

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

    @Mock
    private OrderSellerListRepository orderSellerListRepository;

    private MockHttpServletRequest request = new MockHttpServletRequest();

    @Test
    @DisplayName("유효하지 않은 상품 id로 인해 실패")
    void postTempOrderFail1() {
        //given
        List<TempOrderDto> tempOrderDtoList = new ArrayList<>();
        TempOrderDto tempOrderDto1 = new TempOrderDto(1L, 10);
        TempOrderDto tempOrderDto2 = new TempOrderDto(2L, 5);
        tempOrderDtoList.add(tempOrderDto1);
        tempOrderDtoList.add(tempOrderDto2);
        TempOrderRequest tempOrderRequest = new TempOrderRequest(tempOrderDtoList);

        //when
        //then
        Assertions.assertThatThrownBy(() -> orderService.postTempOrder(tempOrderRequest, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 상품");
    }

    @Test
    @DisplayName("유효하지 않은 상품이 존재해 실패")
    void postTempOrderFail2() {
        //given
        List<TempOrderDto> tempOrderDtoList = new ArrayList<>();
        TempOrderDto tempOrderDto1 = new TempOrderDto(1L, 10);
        TempOrderDto tempOrderDto2 = new TempOrderDto(2L, 5);
        tempOrderDtoList.add(tempOrderDto1);
        tempOrderDtoList.add(tempOrderDto2);
        TempOrderRequest tempOrderRequest = new TempOrderRequest(tempOrderDtoList);

        List<Product> productList = new ArrayList<>();
        Product product1 = new Product(1L, null, null, null, null, null, null, 0, 0, null, null, null, null, null, null, null, null, Status.NORMAL, null, null);
        Product product2 = new Product(1L, null, null, null, null, null, null, 0, 0, null, null, null, null, null, null, null, null, Status.HIDDEN, null, null);
        productList.add(product1);
        productList.add(product2);
        given(productRepository.findAllById(any())).willReturn(productList);

        given(orderRepository.save(any())).willReturn(new Order(1L, null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, null, null, null));

        //when
        //then
        Assertions.assertThatThrownBy(() -> orderService.postTempOrder(tempOrderRequest, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상품 상태");
    }
    @Test
    @DisplayName("임시 주문 등록에 성공")
    void postTempOrderSuccess() {
        //given
        List<TempOrderDto> tempOrderDtoList = new ArrayList<>();
        TempOrderDto tempOrderDto1 = new TempOrderDto(1L, 10);
        TempOrderDto tempOrderDto2 = new TempOrderDto(2L, 5);
        tempOrderDtoList.add(tempOrderDto1);
        tempOrderDtoList.add(tempOrderDto2);
        TempOrderRequest tempOrderRequest = new TempOrderRequest(tempOrderDtoList);

        List<Product> productList = new ArrayList<>();
        Product product1 = new Product(1L, null, null, "상품명1", null, null, null, 0, 10000, null, null, null, null, null, null, null, null, Status.NORMAL, null, null);
        Product product2 = new Product(1L, null, null, "상품명2", null, null, null, 0, 12000, null, null, null, null, null, null, null, null, Status.NORMAL, null, null);
        productList.add(product1);
        productList.add(product2);
        given(productRepository.findAllById(any())).willReturn(productList);

        given(orderRepository.save(any())).willReturn(new Order(1L, null, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null, null, null, null));

        //when
        TempOrderResponse tempOrderResponse = orderService.postTempOrder(tempOrderRequest, request);
        //then
        Assertions.assertThat(tempOrderResponse.getOrderId()).isNotNull();
    }
}