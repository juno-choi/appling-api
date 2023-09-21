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
                        fieldWithPath("data.order_id").type(JsonFieldType.NUMBER).description("주문 id"),
                        fieldWithPath("data.total_price").type(JsonFieldType.NUMBER).description("주문 총 가격"),
                        fieldWithPath("data.receipt_list").type(JsonFieldType.ARRAY).description("영수증 리스트"),
                        fieldWithPath("data.receipt_list[].seller").type(JsonFieldType.OBJECT).description("판매자 정보"),
                        fieldWithPath("data.receipt_list[].seller.seller_id").type(JsonFieldType.NUMBER).description("판매자 id"),
                        fieldWithPath("data.receipt_list[].seller.email").type(JsonFieldType.STRING).description("판매자 email"),
                        fieldWithPath("data.receipt_list[].seller.company").type(JsonFieldType.STRING).description("판매자 회사명"),
                        fieldWithPath("data.receipt_list[].seller.zonecode").type(JsonFieldType.STRING).description("판매자 우편주소"),
                        fieldWithPath("data.receipt_list[].seller.address").type(JsonFieldType.STRING).description("판매자 주소"),
                        fieldWithPath("data.receipt_list[].seller.address_detail").type(JsonFieldType.STRING).description("판매자 상세 주소"),
                        fieldWithPath("data.receipt_list[].seller.tel").type(JsonFieldType.STRING).description("판매자 연락처"),
                        fieldWithPath("data.receipt_list[].product_name").type(JsonFieldType.STRING).description("상품명"),
                        fieldWithPath("data.receipt_list[].ea").type(JsonFieldType.NUMBER).description("주문 개수"),
                        fieldWithPath("data.receipt_list[].total_price").type(JsonFieldType.NUMBER).description("상품 주문 금액"),
                        fieldWithPath("data.order_list[].product_id").type(JsonFieldType.NUMBER).description("상품 id"),
                        fieldWithPath("data.order_list[].ea").type(JsonFieldType.NUMBER).description("상품 주문 개수"),
                        fieldWithPath("data.order_list[].main_explanation").type(JsonFieldType.STRING).description("메인 설명"),
                        fieldWithPath("data.order_list[].product_main_explanation").type(JsonFieldType.STRING).description("상품 메인 설명"),
                        fieldWithPath("data.order_list[].product_sub_explanation").type(JsonFieldType.STRING).description("상품 서브 설명"),
                        fieldWithPath("data.order_list[].origin_price").type(JsonFieldType.NUMBER).description("상품 원가"),
                        fieldWithPath("data.order_list[].price").type(JsonFieldType.NUMBER).description("상품 판매가"),
                        fieldWithPath("data.order_list[].purchase_inquiry").type(JsonFieldType.STRING).description("상품 주의 사항 및 보관 방법"),
                        fieldWithPath("data.order_list[].origin").type(JsonFieldType.STRING).description("원산지"),
                        fieldWithPath("data.order_list[].producer").type(JsonFieldType.STRING).description("생산자"),
                        fieldWithPath("data.order_list[].main_title").type(JsonFieldType.STRING).description("주문 상품"),
                        fieldWithPath("data.order_list[].main_image").type(JsonFieldType.STRING).description("메인 이미지"),
                        fieldWithPath("data.order_list[].image1").type(JsonFieldType.STRING).description("이미지1"),
                        fieldWithPath("data.order_list[].image2").type(JsonFieldType.STRING).description("이미지2"),
                        fieldWithPath("data.order_list[].image3").type(JsonFieldType.STRING).description("이미지3"),
                        fieldWithPath("data.order_list[].view_cnt").type(JsonFieldType.NUMBER).description("조회수"),
                        fieldWithPath("data.order_list[].status").type(JsonFieldType.STRING).description("상품 상태값"),
                        fieldWithPath("data.order_list[].created_at").type(JsonFieldType.STRING).description("상품 등록일"),
                        fieldWithPath("data.order_list[].modified_at").type(JsonFieldType.STRING).description("상품 수정일"),
                        fieldWithPath("data.order_list[].seller").type(JsonFieldType.OBJECT).description("판매자"),
                        fieldWithPath("data.order_list[].seller.seller_id").type(JsonFieldType.NUMBER).description("판매자 id"),
                        fieldWithPath("data.order_list[].seller.email").type(JsonFieldType.STRING).description("판매자 email"),
                        fieldWithPath("data.order_list[].seller.company").type(JsonFieldType.STRING).description("판매자 회사명"),
                        fieldWithPath("data.order_list[].seller.zonecode").type(JsonFieldType.STRING).description("판매자 우편주소"),
                        fieldWithPath("data.order_list[].seller.address").type(JsonFieldType.STRING).description("판매자 주소"),
                        fieldWithPath("data.order_list[].seller.address_detail").type(JsonFieldType.STRING).description("판매자 상세 주소"),
                        fieldWithPath("data.order_list[].seller.tel").type(JsonFieldType.STRING).description("판매자 연락처"),
                        fieldWithPath("data.order_list[].category").type(JsonFieldType.OBJECT).description("카테고리"),
                        fieldWithPath("data.order_list[].category.category_id").type(JsonFieldType.NUMBER).description("카테고리 id"),
                        fieldWithPath("data.order_list[].category.name").type(JsonFieldType.STRING).description("카테고리명"),
                        fieldWithPath("data.order_list[].category.created_at").type(JsonFieldType.STRING).description("카테고리 등록일"),
                        fieldWithPath("data.order_list[].category.modified_at").type(JsonFieldType.STRING).description("카테고리 수정일")
                )
        ));
    }
}