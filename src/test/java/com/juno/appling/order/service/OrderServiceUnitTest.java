package com.juno.appling.order.service;

import com.juno.appling.global.util.MemberUtil;
import com.juno.appling.member.domain.Member;
import com.juno.appling.member.enums.Role;
import com.juno.appling.order.domain.Order;
import com.juno.appling.order.infrastructure.OrderItemRepository;
import com.juno.appling.order.infrastructure.OrderRepository;
import com.juno.appling.order.controller.request.CompleteOrderRequest;
import com.juno.appling.order.controller.request.TempOrderDto;
import com.juno.appling.order.controller.request.TempOrderRequest;
import com.juno.appling.order.controller.response.CompleteOrderResponse;
import com.juno.appling.order.controller.response.PostTempOrderResponse;
import com.juno.appling.order.enums.OrderStatus;
import com.juno.appling.product.domain.Product;
import com.juno.appling.product.enums.ProductType;
import com.juno.appling.product.infrastructure.ProductRepository;
import com.juno.appling.product.enums.ProductStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.juno.appling.Base.PRODUCT_ID_APPLE;
import static com.juno.appling.Base.PRODUCT_ID_PEAR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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


    private MockHttpServletRequest request = new MockHttpServletRequest();

    @Test
    @DisplayName("유효하지 않은 상품 id로 인해 실패")
    void postTempOrderFail1() {
        //given
        List<TempOrderDto> tempOrderDtoList = new ArrayList<>();
        TempOrderDto tempOrderDto1 = TempOrderDto.builder()
            .productId(PRODUCT_ID_APPLE)
            .ea(3)
            .build();
        TempOrderDto tempOrderDto2 = TempOrderDto.builder()
            .productId(PRODUCT_ID_PEAR)
            .ea(10)
            .build();
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
        TempOrderDto tempOrderDto1 = TempOrderDto.builder()
            .productId(PRODUCT_ID_APPLE)
            .ea(5)
            .build();
        TempOrderDto tempOrderDto2 = TempOrderDto.builder()
            .productId(PRODUCT_ID_PEAR)
            .ea(10)
            .build();
        tempOrderDtoList.add(tempOrderDto1);
        tempOrderDtoList.add(tempOrderDto2);
        TempOrderRequest tempOrderRequest = new TempOrderRequest(tempOrderDtoList);

        List<Product> productList = new ArrayList<>();
        Product product1 = Product.builder()
            .id(PRODUCT_ID_APPLE)
            .ea(10)
            .build();
        Product product2 = Product.builder()
            .id(PRODUCT_ID_APPLE)
            .ea(10)
            .build();
        productList.add(product1);
        productList.add(product2);
        given(productRepository.findAllById(any())).willReturn(productList);

        given(orderRepository.save(any())).willReturn(new Order(1L, null, null, new ArrayList<>(), null, null, null, null, null));

        //when
        //then
        Assertions.assertThatThrownBy(() -> orderService.postTempOrder(tempOrderRequest, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("상품 상태");
    }

    @Test
    @DisplayName("옵션 번호가 유효하지 않아 상품 임시 주문 등록에 실패")
    void postTempOrderFail3() {
        //given
        List<TempOrderDto> tempOrderDtoList = new ArrayList<>();
        TempOrderDto tempOrderDto1 = TempOrderDto.builder()
            .productId(PRODUCT_ID_APPLE)
            .ea(10)
            .optionId(300L)
            .build();
        TempOrderDto tempOrderDto2 = TempOrderDto.builder()
            .productId(PRODUCT_ID_PEAR)
            .ea(5)
            .build();
        tempOrderDtoList.add(tempOrderDto1);
        tempOrderDtoList.add(tempOrderDto2);
        TempOrderRequest tempOrderRequest = new TempOrderRequest(tempOrderDtoList);

        List<Product> productList = new ArrayList<>();
        Product product1 = Product.builder()
            .id(PRODUCT_ID_APPLE)
            .mainTitle("상풍명1")
            .price(10000)
            .ea(10)
            .status(ProductStatus.NORMAL)
            .type(ProductType.OPTION)
            .build();
        product1.addAllOptionsList(new ArrayList<>());

        Product product2 = Product.builder()
            .id(PRODUCT_ID_PEAR)
            .mainTitle("상풍명2")
            .price(10000)
            .status(ProductStatus.NORMAL)
            .ea(10)
            .build();

        productList.add(product1);
        productList.add(product2);
        given(productRepository.findAllById(any())).willReturn(productList);

        given(orderRepository.save(any())).willReturn(new Order(1L, null, null, new ArrayList<>(), null, null, null, null, null));

        //when
        //then
        Assertions.assertThatThrownBy(() -> orderService.postTempOrder(tempOrderRequest, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("optionId");
    }

    @Test
    @DisplayName("상품 재고가 부족하여 상품 임시 주문 등록에 실패")
    void postTempOrderFail4() {
        //given
        List<TempOrderDto> tempOrderDtoList = new ArrayList<>();
        TempOrderDto tempOrderDto1 = TempOrderDto.builder()
            .productId(PRODUCT_ID_APPLE)
            .ea(20)
            .build();
        TempOrderDto tempOrderDto2 = TempOrderDto.builder()
            .productId(PRODUCT_ID_PEAR)
            .ea(5)
            .build();
        tempOrderDtoList.add(tempOrderDto1);
        tempOrderDtoList.add(tempOrderDto2);
        TempOrderRequest tempOrderRequest = new TempOrderRequest(tempOrderDtoList);

        List<Product> productList = new ArrayList<>();
        Product product1 = Product.builder()
            .id(PRODUCT_ID_APPLE)
            .mainTitle("상풍명1")
            .price(10000)
            .ea(0)
            .status(ProductStatus.NORMAL)
            .build();
        Product product2 = Product.builder()
            .id(PRODUCT_ID_PEAR)
            .mainTitle("상풍명2")
            .price(10000)
            .ea(0)
            .status(ProductStatus.NORMAL)
            .build();
        productList.add(product1);
        productList.add(product2);
        given(productRepository.findAllById(any())).willReturn(productList);

        given(orderRepository.save(any())).willReturn(new Order(1L, null, null, new ArrayList<>(), null, null, null, null, null));

        //when
        //then
        Assertions.assertThatThrownBy(() -> orderService.postTempOrder(tempOrderRequest, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("재고");
    }

    @Test
    @DisplayName("임시 주문 등록에 성공")
    void postTempOrderSuccess() {
        //given
        List<TempOrderDto> tempOrderDtoList = new ArrayList<>();
        TempOrderDto tempOrderDto1 = TempOrderDto.builder()
            .productId(PRODUCT_ID_APPLE)
            .ea(10)
            .build();
        TempOrderDto tempOrderDto2 = TempOrderDto.builder()
            .productId(PRODUCT_ID_PEAR)
            .ea(5)
            .build();
        tempOrderDtoList.add(tempOrderDto1);
        tempOrderDtoList.add(tempOrderDto2);
        TempOrderRequest tempOrderRequest = new TempOrderRequest(tempOrderDtoList);

        List<Product> productList = new ArrayList<>();
        Product product1 = Product.builder()
            .id(PRODUCT_ID_APPLE)
            .mainTitle("상풍명1")
            .price(10000)
            .status(ProductStatus.NORMAL)
            .ea(10)
            .build();
        Product product2 = Product.builder()
            .id(PRODUCT_ID_PEAR)
            .mainTitle("상풍명2")
            .price(10000)
            .status(ProductStatus.NORMAL)
            .ea(10)
            .build();
        productList.add(product1);
        productList.add(product2);
        given(productRepository.findAllById(any())).willReturn(productList);

        given(orderRepository.save(any())).willReturn(new Order(1L, null, null, new ArrayList<>(), null, null, null, null, null));

        //when
        PostTempOrderResponse postTempOrderResponse = orderService.postTempOrder(tempOrderRequest, request);
        //then
        Assertions.assertThat(postTempOrderResponse.getOrderId()).isNotNull();
    }

    @Test
    @DisplayName("임시 주문 불러오기에 유효하지 않은 order id로 실패")
    void getTempOrderFail1() {
        //given
        given(orderRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));
        //when
        //then
        Assertions.assertThatThrownBy(() -> orderService.getTempOrder(0L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 주문 번호");
    }

    @Test
    @DisplayName("임시 주문 불러오기에 주문을 요청한 member id가 아닌 경우 실패")
    void getTempOrderFail2() {
        //given
        Long orderId = 1L;
        Long memberId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Member member1 = new Member(memberId, "emial@mail.com", "password", "nickname", "name", "19991030", Role.SELLER, null, null, now, now);
        Member member2 = new Member(2L, "emial@mail.com", "password", "nickname", "name", "19991030", Role.SELLER, null, null, now, now);
        given(memberUtil.getMember(any())).willReturn(member1);
        given(orderRepository.findById(anyLong())).willReturn(Optional.ofNullable(new Order(orderId, member2, null, new ArrayList<>(), null, OrderStatus.TEMP, "", now, now)));
        //when
        //then
        Assertions.assertThatThrownBy(() -> orderService.getTempOrder(orderId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 주문");
    }

    @Test
    @DisplayName("임시 주문 불러오기에 주문 status가 temp가 아닌 경우 실패")
    void getTempOrderFail3() {
        //given
        Long orderId = 1L;
        Long memberId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Member member1 = new Member(memberId, "emial@mail.com", "password", "nickname", "name", "19991030", Role.SELLER, null, null, now, now);
        given(memberUtil.getMember(any())).willReturn(member1);
        given(orderRepository.findById(anyLong())).willReturn(Optional.ofNullable(new Order(orderId, member1, null, new ArrayList<>(), null, OrderStatus.ORDER, "", now, now)));
        //when
        //then
        Assertions.assertThatThrownBy(() -> orderService.getTempOrder(orderId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 주문");
    }

    @Test
    @DisplayName("주문 완료 성공")
    void completeOrderSuccess() {
        //given
        Long orderId = 1L;
        LocalDateTime now = LocalDateTime.now();
        Member member = new Member(1L, "emial@mail.com", "password", "nickname", "name", "19991030", Role.SELLER, null, null, now, now);
        CompleteOrderRequest completeOrderRequest = new CompleteOrderRequest(orderId, "주문자", "주문자 우편번호", "주문자 주소", "주문자 상세 주소", "주문자 연락처", "수령인", "수령인 우편번호", "수령인 주소", "수령인 상세 주소", "수령인 연락처");
        given(orderRepository.findById(anyLong())).willReturn(Optional.ofNullable(new Order(orderId, member, null, new ArrayList<>(), null, OrderStatus.TEMP, "", now, now)));
        given(memberUtil.getMember(any())).willReturn(member);
        //when
        CompleteOrderResponse completeOrderResponse = orderService.completeOrder(completeOrderRequest, request);
        //then
        Assertions.assertThat(completeOrderResponse.getOrderNumber()).contains("ORDER-");
    }

}