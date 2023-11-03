package com.juno.appling.order.service;

import static com.juno.appling.Base.PRODUCT_ID_APPLE;
import static com.juno.appling.Base.PRODUCT_ID_PEAR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.github.dockerjava.api.exception.UnauthorizedException;
import com.juno.appling.global.util.MemberUtil;
import com.juno.appling.member.domain.entity.MemberEntity;
import com.juno.appling.member.enums.MemberRole;
import com.juno.appling.member.repository.SellerJpaRepository;
import com.juno.appling.order.controller.request.CompleteOrderRequest;
import com.juno.appling.order.controller.request.TempOrderDto;
import com.juno.appling.order.controller.request.TempOrderRequest;
import com.juno.appling.order.controller.response.CompleteOrderResponse;
import com.juno.appling.order.controller.response.PostTempOrderResponse;
import com.juno.appling.order.domain.entity.OrderEntity;
import com.juno.appling.order.domain.entity.OrderItemEntity;
import com.juno.appling.order.domain.model.Order;
import com.juno.appling.order.enums.OrderStatus;
import com.juno.appling.order.repository.DeliveryJpaRepository;
import com.juno.appling.order.repository.OrderItemJpaRepository;
import com.juno.appling.order.repository.OrderJpaRepository;
import com.juno.appling.product.domain.entity.OptionEntity;
import com.juno.appling.product.domain.entity.ProductEntity;
import com.juno.appling.product.enums.ProductStatus;
import com.juno.appling.product.enums.ProductType;
import com.juno.appling.product.repository.ProductJpaRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;

@ExtendWith({MockitoExtension.class})
class OrderServiceUnitTest {

    @InjectMocks
    private OrderServiceImpl orderServiceImpl;

    @Mock
    private ProductJpaRepository productJpaRepository;

    @Mock
    private MemberUtil memberUtil;

    @Mock
    private OrderJpaRepository orderJpaRepository;

    @Mock
    private OrderItemJpaRepository orderItemJpaRepository;

    @Mock
    private DeliveryJpaRepository deliveryJpaRepository;

    @Mock
    private SellerJpaRepository sellerJpaRepository;

    private MockHttpServletRequest request = new MockHttpServletRequest();


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

        List<ProductEntity> productEntityList = new ArrayList<>();
        ProductEntity productEntity1 = ProductEntity.builder()
            .id(PRODUCT_ID_APPLE)
            .ea(10)
            .build();
        ProductEntity productEntity2 = ProductEntity.builder()
            .id(PRODUCT_ID_APPLE)
            .ea(10)
            .build();
        productEntityList.add(productEntity1);
        productEntityList.add(productEntity2);
        given(productJpaRepository.findAllById(any())).willReturn(productEntityList);

        given(orderJpaRepository.save(any())).willReturn(OrderEntity.from(Order.builder().build()));

        //when
        //then
        assertThatThrownBy(() -> orderServiceImpl.postTempOrder(tempOrderRequest, request))
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

        List<ProductEntity> productEntityList = new ArrayList<>();
        ProductEntity productEntity1 = ProductEntity.builder()
            .id(PRODUCT_ID_APPLE)
            .mainTitle("상풍명1")
            .price(10000)
            .ea(10)
            .status(ProductStatus.NORMAL)
            .type(ProductType.OPTION)
            .build();
        productEntity1.addAllOptionsList(new ArrayList<>());

        ProductEntity productEntity2 = ProductEntity.builder()
            .id(PRODUCT_ID_PEAR)
            .mainTitle("상풍명2")
            .price(10000)
            .status(ProductStatus.NORMAL)
            .ea(10)
            .build();

        productEntityList.add(productEntity1);
        productEntityList.add(productEntity2);
        given(productJpaRepository.findAllById(any())).willReturn(productEntityList);

        given(orderJpaRepository.save(any())).willReturn(OrderEntity.from(Order.builder().build()));

        //when
        //then
        assertThatThrownBy(() -> orderServiceImpl.postTempOrder(tempOrderRequest, request))
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

        List<ProductEntity> productEntityList = new ArrayList<>();
        ProductEntity productEntity1 = ProductEntity.builder()
            .id(PRODUCT_ID_APPLE)
            .mainTitle("상풍명1")
            .price(10000)
            .ea(0)
            .status(ProductStatus.NORMAL)
            .build();
        ProductEntity productEntity2 = ProductEntity.builder()
            .id(PRODUCT_ID_PEAR)
            .mainTitle("상풍명2")
            .price(10000)
            .ea(0)
            .status(ProductStatus.NORMAL)
            .build();
        productEntityList.add(productEntity1);
        productEntityList.add(productEntity2);
        given(productJpaRepository.findAllById(any())).willReturn(productEntityList);

        given(orderJpaRepository.save(any())).willReturn(OrderEntity.from(Order.builder().id(1L).build()));

        //when
        //then
        assertThatThrownBy(() -> orderServiceImpl.postTempOrder(tempOrderRequest, request))
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

        List<ProductEntity> productEntityList = new ArrayList<>();
        ProductEntity productEntity1 = ProductEntity.builder()
            .id(PRODUCT_ID_APPLE)
            .mainTitle("상풍명1")
            .price(10000)
            .status(ProductStatus.NORMAL)
            .ea(10)
            .build();
        ProductEntity productEntity2 = ProductEntity.builder()
            .id(PRODUCT_ID_PEAR)
            .mainTitle("상풍명2")
            .price(10000)
            .status(ProductStatus.NORMAL)
            .ea(10)
            .build();
        productEntityList.add(productEntity1);
        productEntityList.add(productEntity2);
        given(productJpaRepository.findAllById(any())).willReturn(productEntityList);
        OrderEntity orderEntity = OrderEntity.from(
            Order.builder()
                .id(1L)
                .orderItemList(new ArrayList<>())
                .deliveryList(new ArrayList<>())
                .build()
        );

        given(orderJpaRepository.save(any())).willReturn(
            orderEntity
        );

        //when
        PostTempOrderResponse postTempOrderResponse = orderServiceImpl.postTempOrder(tempOrderRequest, request);
        //then
        assertThat(postTempOrderResponse.getOrderId()).isNotNull();
    }

    @Test
    @DisplayName("임시 주문 불러오기에 유효하지 않은 order id로 실패")
    void getTempOrderFail1() {
        //given
        given(orderJpaRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));
        //when
        //then
        assertThatThrownBy(() -> orderServiceImpl.getTempOrder(0L, request))
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
        MemberEntity memberEntity1 = new MemberEntity(memberId, "emial@mail.com", "password", "nickname", "name", "19991030", MemberRole.SELLER, null, null, now, now);
        MemberEntity memberEntity2 = new MemberEntity(2L, "emial@mail.com", "password", "nickname", "name", "19991030", MemberRole.SELLER, null, null, now, now);
        given(memberUtil.getMember(any())).willReturn(memberEntity1);

        given(orderJpaRepository.findById(anyLong())).willReturn(Optional.ofNullable(
            OrderEntity.from(Order.builder()
                .id(orderId)
                .member(memberEntity2.toModel())
                .status(OrderStatus.TEMP)
                .createdAt(now)
                .modifiedAt(now)
                .build())
        ));
        //when
        //then
        assertThatThrownBy(() -> orderServiceImpl.getTempOrder(orderId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 주문");
    }


    @Test
    @DisplayName("주문 자체에 유효하지 않은 option id 값으로 인해 주문 완료 실패")
    void completeOrderFail1() {
        //given
        Long orderId = 1L;
        LocalDateTime now = LocalDateTime.now();
        MemberEntity memberEntity = new MemberEntity(1L, "emial@mail.com", "password", "nickname", "name", "19991030", MemberRole.SELLER, null, null, now, now);
        CompleteOrderRequest completeOrderRequest = CompleteOrderRequest.builder()
            .orderId(orderId)
            .ownerName("주문자")
            .ownerZonecode("1234567")
            .ownerAddress("주문자 주소")
            .ownerAddressDetail("주문자 상세 주소")
            .ownerTel("주문자 연락처")
            .recipientName("수령인")
            .recipientZonecode("1234567")
            .recipientAddress("수령인 주소")
            .recipientAddressDetail("수령인 상세 주소")
            .recipientTel("수령인 연락처")
            .build();
        ProductEntity productEntity = ProductEntity.builder()
                .id(PRODUCT_ID_APPLE)
                .mainTitle("상풍명1")
                .price(10000)
                .status(ProductStatus.NORMAL)
                .ea(10)
                .build();

        OrderEntity orderEntity = OrderEntity.from(Order.builder()
            .id(orderId)
            .member(memberEntity.toModel())
            .status(OrderStatus.TEMP)
            .createdAt(now)
            .modifiedAt(now)
            .build());
        List<OrderItemEntity> orderItemEntityList = new ArrayList<>();
        OrderItemEntity option1 = OrderItemEntity.of(orderEntity,productEntity, new OptionEntity("option1", 1000, 100, productEntity), 1);
        orderItemEntityList.add(option1);

        given(orderJpaRepository.findById(anyLong())).willReturn(Optional.ofNullable(orderEntity));
        given(memberUtil.getMember(any())).willReturn(memberEntity);
        //when
        CompleteOrderResponse completeOrderResponse = orderServiceImpl.completeOrder(completeOrderRequest, request);
        //then
        assertThat(completeOrderResponse.getOrderNumber()).contains("ORDER-");
    }
    @Test
    @DisplayName("주문 완료 성공")
    void completeOrderSuccess() {
        //given
        Long orderId = 1L;
        LocalDateTime now = LocalDateTime.now();
        MemberEntity memberEntity = new MemberEntity(1L, "emial@mail.com", "password", "nickname", "name", "19991030", MemberRole.SELLER, null, null, now, now);
        CompleteOrderRequest completeOrderRequest = CompleteOrderRequest.builder()
            .orderId(orderId)
            .ownerName("주문자")
            .ownerZonecode("1234567")
            .ownerAddress("주문자 주소")
            .ownerAddressDetail("주문자 상세 주소")
            .ownerTel("주문자 연락처")
            .recipientName("수령인")
            .recipientZonecode("1234567")
            .recipientAddress("수령인 주소")
            .recipientAddressDetail("수령인 상세 주소")
            .recipientTel("수령인 연락처")
            .build();
        given(orderJpaRepository.findById(anyLong())).willReturn(Optional.ofNullable(
            OrderEntity.from(Order.builder()
                .id(orderId)
                .member(memberEntity.toModel())
                .status(OrderStatus.TEMP)
                .createdAt(now)
                .modifiedAt(now)
                .build())
        ));
        given(memberUtil.getMember(any())).willReturn(memberEntity);
        //when
        CompleteOrderResponse completeOrderResponse = orderServiceImpl.completeOrder(completeOrderRequest, request);
        //then
        assertThat(completeOrderResponse.getOrderNumber()).contains("ORDER-");
    }

    @Test
    @DisplayName("seller가 아닌 관리자 주문 내역 불러오기 실패")
    void getOrderListBySellerFail1() {
        //given
        Long memberId = 1L;
        LocalDateTime now = LocalDateTime.now();
        MemberEntity memberEntity1 = new MemberEntity(memberId, "emial@mail.com", "password", "nickname", "name", "19991030", MemberRole.SELLER, null, null, now, now);
        given(memberUtil.getMember(any())).willReturn(memberEntity1);
        given(sellerJpaRepository.findByMember(any(MemberEntity.class))).willReturn(Optional.ofNullable(null));
        //when
        //then
        assertThatThrownBy(() -> orderServiceImpl.getOrderListBySeller(Pageable.ofSize(10), "", "COMPLETE", request))
            .isInstanceOf(UnauthorizedException.class)
            .hasMessageContaining("잘못된 접근")
        ;

    }
}