package com.juno.appling.order.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.juno.appling.global.util.MemberUtil;
import com.juno.appling.member.domain.Member;
import com.juno.appling.member.domain.Seller;
import com.juno.appling.member.dto.request.JoinRequest;
import com.juno.appling.member.enums.Role;
import com.juno.appling.order.domain.Orders;
import com.juno.appling.order.domain.OrdersDetail;
import com.juno.appling.order.domain.OrdersDetailRepository;
import com.juno.appling.order.domain.OrdersRepository;
import com.juno.appling.order.dto.request.TempOrderDto;
import com.juno.appling.order.dto.request.TempOrderRequest;
import com.juno.appling.order.dto.response.TempOrderResponse;
import com.juno.appling.order.enums.OrdersStatus;
import com.juno.appling.product.domain.Category;
import com.juno.appling.product.domain.Product;
import com.juno.appling.product.domain.ProductRepository;
import com.juno.appling.product.enums.CategoryStatus;
import com.juno.appling.product.enums.Status;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
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
    private OrdersRepository ordersRepository;

    @Mock
    private OrdersDetailRepository ordersDetailRepository;

    private MockHttpServletRequest request = new MockHttpServletRequest();

    @Test
    @DisplayName("유효하지 않은 상품 id로 인해 실패")
    void postTempOrderFail1() {
        //given
        TempOrderDto tempOrderDto = new TempOrderDto(1L, 5);
        List<TempOrderDto> tempOrderDtoList = new ArrayList<>();
        tempOrderDtoList.add(tempOrderDto);
        TempOrderRequest tempOrderRequest = new TempOrderRequest(tempOrderDtoList);
        //when
        //then
        assertThatThrownBy(() -> orderService.postTempOrder(tempOrderRequest, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("유효하지 않은 상품 id");
    }

    @Test
    @DisplayName("유효하지 않은 상품이 존재해 실패")
    void postTempOrderFail2() {
        //given
        TempOrderDto tempOrderDto1 = new TempOrderDto(1L, 5);
        TempOrderDto tempOrderDto2 = new TempOrderDto(2L, 3);
        List<TempOrderDto> tempOrderDtoList = new ArrayList<>();
        tempOrderDtoList.add(tempOrderDto1);
        tempOrderDtoList.add(tempOrderDto2);
        TempOrderRequest tempOrderRequest = new TempOrderRequest(tempOrderDtoList);
        LocalDateTime now = LocalDateTime.now();

        Member member = new Member(1L, "email@mail.com", "password", "nickname", "name",
            "19941030",
            Role.SELLER, null, null, now, now);
        given(memberUtil.getMember(any())).willReturn(member);
        List<Product> productList = new ArrayList<>();
        Seller seller = new Seller(1L, member, "company", "01012341234", "우편주소", "주소", "mail@mail.com");
        Category category = Category.of("카테고리", CategoryStatus.USE);

        Product product1 = new Product(1L, seller, category, "메인", "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000, 9000,
            "주의사항", "원산지", "공급자", "https://s3.image.com", "https://s3.image.com",
            "https://s3.image.com", "https://s3.image.com", 0L,
            Status.HIDDEN, now, now);
        Product product2 = new Product(2L, seller, category, "메인", "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000, 3000,
            "주의사항", "원산지", "공급자", "https://s3.image.com", "https://s3.image.com",
            "https://s3.image.com", "https://s3.image.com", 0L,
            Status.NORMAL, now, now);
        productList.add(product1);
        productList.add(product2);
        given(productRepository.findAllById(any())).willReturn(productList);
        //when
        //then
        Assertions.assertThatThrownBy(() -> orderService.postTempOrder(tempOrderRequest, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("유효하지 않은 상품이 존재");
    }
    @Test
    @DisplayName("임시 주문 등록에 성공")
    void postTempOrderSuccess() {
        //given
        TempOrderDto tempOrderDto1 = new TempOrderDto(1L, 5);
        TempOrderDto tempOrderDto2 = new TempOrderDto(2L, 3);
        List<TempOrderDto> tempOrderDtoList = new ArrayList<>();
        tempOrderDtoList.add(tempOrderDto1);
        tempOrderDtoList.add(tempOrderDto2);
        TempOrderRequest tempOrderRequest = new TempOrderRequest(tempOrderDtoList);

        List<Product> productList = new ArrayList<>();
        JoinRequest joinRequest = new JoinRequest("email@mail.com", "passowrd", "tester",
            "nickname", "19991030");
        Member member = Member.of(joinRequest);
        Seller seller = new Seller(1L, member, "company", "01012341234", "우편주소", "주소", "mail@mail.com");
        Category category = Category.of("카테고리", CategoryStatus.USE);
        LocalDateTime now = LocalDateTime.now();
        Product product1 = new Product(1L, seller, category, "메인", "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000, 9000,
            "주의사항", "원산지", "공급자", "https://s3.image.com", "https://s3.image.com",
            "https://s3.image.com", "https://s3.image.com", 0L,
            Status.NORMAL, now, now);
        Product product2 = new Product(2L, seller, category, "메인", "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000, 3000,
            "주의사항", "원산지", "공급자", "https://s3.image.com", "https://s3.image.com",
            "https://s3.image.com", "https://s3.image.com", 0L,
            Status.NORMAL, now, now);
        productList.add(product1);
        productList.add(product2);
        given(productRepository.findAllById(any())).willReturn(productList);
        Orders orders = new Orders(1L, member, OrdersStatus.TEMP, null, null, null, null, null, null, null, null, 0, null, now, now);
        given(ordersRepository.save(any())).willReturn(orders);
        given(ordersDetailRepository.save(any())).willReturn(new OrdersDetail());
        //when
        TempOrderResponse tempOrderResponse = orderService.postTempOrder(tempOrderRequest, request);
        //then
        int totalPrice = 0;
        totalPrice += product1.getPrice() * tempOrderDto1.getEa();
        totalPrice += product2.getPrice() * tempOrderDto2.getEa();
        Assertions.assertThat(tempOrderResponse.getTotalPrice()).isEqualTo(totalPrice);
    }
}