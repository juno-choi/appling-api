package com.juno.appling.order.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.juno.appling.global.util.MemberUtil;
import com.juno.appling.member.domain.Member;
import com.juno.appling.member.domain.Seller;
import com.juno.appling.member.dto.request.JoinRequest;
import com.juno.appling.order.dto.request.TempOrder;
import com.juno.appling.order.dto.request.TempOrderRequest;
import com.juno.appling.product.domain.Category;
import com.juno.appling.product.domain.Product;
import com.juno.appling.product.domain.ProductRepository;
import com.juno.appling.product.dto.request.ProductRequest;
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

    private MockHttpServletRequest request = new MockHttpServletRequest();

    @Test
    @DisplayName("유효하지 않은 상품 id로 인해 실패")
    void postTempOrderFail2() {
        //given
        TempOrder tempOrder = new TempOrder(1L, 5);
        List<TempOrder> tempOrderList = new ArrayList<>();
        tempOrderList.add(tempOrder);
        TempOrderRequest tempOrderRequest = new TempOrderRequest(tempOrderList);
        //when
        //then
        assertThatThrownBy(() -> orderService.postTempOrder(tempOrderRequest, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("유효하지 않은 상품 id");
    }

    @Test
    @DisplayName("임시 주문 등록에 성공")
    void postTempOrderSuccess() {
        //given
        TempOrder tempOrder1 = new TempOrder(1L, 5);
        TempOrder tempOrder2 = new TempOrder(5L, 3);
        List<TempOrder> tempOrderList = new ArrayList<>();
        tempOrderList.add(tempOrder1);
        tempOrderList.add(tempOrder2);
        TempOrderRequest tempOrderRequest = new TempOrderRequest(tempOrderList);

        List<Product> productList = new ArrayList<>();
        JoinRequest joinRequest = new JoinRequest("email@mail.com", "passowrd", "tester",
            "nickname", "19991030");
        Member member = Member.of(joinRequest);
        Seller seller = Seller.of(member, "company", "01012341234", "우편주소", "주소", "mail@mail.com");
        Category category = Category.of("카테고리", CategoryStatus.USE);
        LocalDateTime now = LocalDateTime.now();
        Product product1 = new Product(1L, seller, category, "메인", "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000, 9000,
            "주의사항", "원산지", "공급자", "https://s3.image.com", "https://s3.image.com",
            "https://s3.image.com", "https://s3.image.com", 0L,
            Status.NORMAL, now, now);
        Product product2 = new Product(2L, seller, category, "메인", "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000, 9000,
            "주의사항", "원산지", "공급자", "https://s3.image.com", "https://s3.image.com",
            "https://s3.image.com", "https://s3.image.com", 0L,
            Status.NORMAL, now, now);
        productList.add(product1);
        productList.add(product2);
        given(productRepository.findAllById(any())).willReturn(productList);
        //when
        orderService.postTempOrder(tempOrderRequest, request);
        //then
    }
}