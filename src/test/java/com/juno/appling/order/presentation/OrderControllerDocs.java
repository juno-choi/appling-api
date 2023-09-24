package com.juno.appling.order.presentation;

import com.juno.appling.ControllerBaseTest;
import com.juno.appling.member.application.MemberAuthService;
import com.juno.appling.member.domain.Member;
import com.juno.appling.member.domain.MemberRepository;
import com.juno.appling.member.domain.Seller;
import com.juno.appling.member.domain.SellerRepository;
import com.juno.appling.member.dto.request.LoginRequest;
import com.juno.appling.member.dto.response.LoginResponse;
import com.juno.appling.order.dto.request.TempOrderDto;
import com.juno.appling.order.dto.request.TempOrderRequest;
import com.juno.appling.product.domain.Category;
import com.juno.appling.product.domain.CategoryRepository;
import com.juno.appling.product.domain.Product;
import com.juno.appling.product.domain.ProductRepository;
import com.juno.appling.product.dto.request.ProductRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class OrderControllerDocs extends ControllerBaseTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberAuthService memberAuthService;

    private ObjectMapper objectMapper = new ObjectMapper();


    private final static String PREFIX = "/api/order";

    @Test
    @DisplayName(PREFIX + " (POST)")
    void getProductList() throws Exception {
        //given
        Member member = memberRepository.findByEmail(SELLER_EMAIL).get();
        Category category = categoryRepository.findById(1L).get();

        ProductRequest searchDto1 = new ProductRequest(1L, "검색 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000,
                8000, "보관 방법", "원산지", "생산자", "https://mainImage", "https://image1", "https://image2",
                "https://image3", "normal");
        ProductRequest searchDto2 = new ProductRequest(1L, "검색 제목2", "메인 설명", "상품 메인 설명", "상품 서브 설명", 15000,
                10000, "보관 방법", "원산지", "생산자", "https://mainImage", "https://image1", "https://image2",
                "https://image3", "normal");

        LoginRequest loginRequest = new LoginRequest(MEMBER_EMAIL, "password");
        LoginResponse login = memberAuthService.login(loginRequest);

        Seller seller = sellerRepository.findByMember(member).get();
        Product saveProduct1 = productRepository.save(Product.of(seller, category, searchDto1));
        Product saveProduct2 = productRepository.save(Product.of(seller, category, searchDto2));

        List<TempOrderDto> orderList = new ArrayList<>();
        int orderEa1 = 3;
        int orderEa2 = 2;
        TempOrderDto order1 = new TempOrderDto(saveProduct1.getId(), orderEa1);
        TempOrderDto order2 = new TempOrderDto(saveProduct2.getId(), orderEa2);
        orderList.add(order1);
        orderList.add(order2);
        TempOrderRequest tempOrderRequest = new TempOrderRequest(orderList);
        //when
        ResultActions perform = mock.perform(
                post(PREFIX).contentType(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION, "Bearer " + login.getAccessToken())
                        .content(objectMapper.writeValueAsString(tempOrderRequest))
        );
        //then
        perform.andExpect(status().is2xxSuccessful());
        perform.andDo(docs.document(
                requestHeaders(
                        headerWithName(AUTHORIZATION).description("access token (MEMBER 권한 이상)")
                ),
                requestFields(
                        fieldWithPath("order_list").type(JsonFieldType.ARRAY).description("주문 리스트"),
                        fieldWithPath("order_list[].product_id").type(JsonFieldType.NUMBER).description("상품 id"),
                        fieldWithPath("order_list[].ea").type(JsonFieldType.NUMBER).description("주문 개수")
                ),
                responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                        fieldWithPath("data.order_id").type(JsonFieldType.NUMBER).description("주문 id")
                )
        ));
    }
}