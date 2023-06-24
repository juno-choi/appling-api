package com.juno.appling.controller.product;

import com.juno.appling.BaseTest;
import com.juno.appling.domain.dto.member.LoginDto;
import com.juno.appling.domain.dto.product.ProductDto;
import com.juno.appling.domain.entity.member.Member;
import com.juno.appling.domain.entity.product.Product;
import com.juno.appling.domain.vo.member.LoginVo;
import com.juno.appling.repository.member.MemberRepository;
import com.juno.appling.repository.product.ProductRepository;
import com.juno.appling.service.member.MemberAuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerDocs extends BaseTest {
    @Autowired
    private MemberAuthService memberAuthService;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ProductRepository productRepository;

    private final static String PREFIX = "/api/seller/product";

    @Test
    @DisplayName(PREFIX)
    void postProduct() throws Exception{
        // given
        LoginDto loginDto = new LoginDto(SELLER_EMAIL, PASSWORD);
        LoginVo login = memberAuthService.login(loginDto);
        ProductDto productDto = new ProductDto("메인 타이틀", "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000, 9000, "취급 방법", "원산지", "공급자", "https://메인이미지", "https://image1", "https://image2", "https://image3");

        // when
        ResultActions perform = mock.perform(
                post(PREFIX)
                        .header(AUTHORIZATION, "Bearer " + login.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDto))
        );
        // then
        perform.andExpect(status().is2xxSuccessful());

        perform.andDo(docs.document(
                requestHeaders(
                        headerWithName(AUTHORIZATION).description("access token")
                ),
                requestFields(
                        fieldWithPath("main_title").type(JsonFieldType.STRING).description("제목"),
                        fieldWithPath("main_explanation").type(JsonFieldType.STRING).description("메인 설명"),
                        fieldWithPath("product_main_explanation").type(JsonFieldType.STRING).description("상품 메인 설명"),
                        fieldWithPath("product_sub_explanation").type(JsonFieldType.STRING).description("상품 보조 설명"),
                        fieldWithPath("origin_price").type(JsonFieldType.NUMBER).description("원가"),
                        fieldWithPath("price").type(JsonFieldType.NUMBER).description("실제 판매가"),
                        fieldWithPath("purchase_inquiry").type(JsonFieldType.STRING).description("취급방법"),
                        fieldWithPath("origin").type(JsonFieldType.STRING).description("원산지"),
                        fieldWithPath("producer").type(JsonFieldType.STRING).description("공급자"),
                        fieldWithPath("main_image").type(JsonFieldType.STRING).description("메인 이미지"),
                        fieldWithPath("image1").type(JsonFieldType.STRING).description("이미지1"),
                        fieldWithPath("image2").type(JsonFieldType.STRING).description("이미지2"),
                        fieldWithPath("image3").type(JsonFieldType.STRING).description("이미지3")
                ),
                responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("등록 상품 id"),
                        fieldWithPath("data.main_title").type(JsonFieldType.STRING).description("메인 타이틀"),
                        fieldWithPath("data.main_explanation").type(JsonFieldType.STRING).description("메인 설명"),
                        fieldWithPath("data.product_main_explanation").type(JsonFieldType.STRING).description("상품 메인 설명"),
                        fieldWithPath("data.product_sub_explanation").type(JsonFieldType.STRING).description("상품 서브 설명"),
                        fieldWithPath("data.origin_price").type(JsonFieldType.NUMBER).description("상품 원가"),
                        fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("상품 실제 판매 가격"),
                        fieldWithPath("data.purchase_inquiry").type(JsonFieldType.STRING).description("취급 방법"),
                        fieldWithPath("data.origin").type(JsonFieldType.STRING).description("원산지"),
                        fieldWithPath("data.producer").type(JsonFieldType.STRING).description("공급자"),
                        fieldWithPath("data.main_image").type(JsonFieldType.STRING).description("메인 이미지 url"),
                        fieldWithPath("data.image1").type(JsonFieldType.STRING).description("이미지1"),
                        fieldWithPath("data.image2").type(JsonFieldType.STRING).description("이미지2"),
                        fieldWithPath("data.image3").type(JsonFieldType.STRING).description("이미지3")
                )
        ));
    }

    @Test
    @DisplayName(PREFIX)
    void getProduct() throws Exception{
        //given
        LoginDto loginDto = new LoginDto(SELLER_EMAIL, PASSWORD);
        LoginVo login = memberAuthService.login(loginDto);

        Member member = memberRepository.findByEmail(SELLER_EMAIL).get();

        ProductDto productDto = new ProductDto("메인 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000, 8000, "보관 방법", "원산지", "생산자", "https://mainImage", "https://image1", "https://image2", "https://image3");
        ProductDto searchDto = new ProductDto("검색 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000, 8000, "보관 방법", "원산지", "생산자", "https://mainImage", "https://image1", "https://image2", "https://image3");
        productRepository.save(Product.of(member, searchDto));

        for(int i=0; i<25; i++){
            productRepository.save(Product.of(member, productDto));
        }

        for(int i=0; i<10; i++){
            productRepository.save(Product.of(member, searchDto));
        }
        //when
        ResultActions perform = mock.perform(
                get(PREFIX)
                        .header(AUTHORIZATION, "Bearer " + login.getAccessToken())
                        .param("search", "검색").param("page", "0").param("size", "5")
        );
        perform.andExpect(status().is2xxSuccessful());
        //then
        perform.andDo(docs.document(
                requestHeaders(
                        headerWithName(AUTHORIZATION).description("access token")
                ),
                queryParameters(
                        parameterWithName("page").description("paging 시작 페이지 번호"),
                        parameterWithName("size").description("paging 시작 페이지 기준 개수 크기"),
                        parameterWithName("search").description("검색어")
                ),
                responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                        fieldWithPath("data.total_page").type(JsonFieldType.NUMBER).description("페이지 총 개수"),
                        fieldWithPath("data.total_elements").type(JsonFieldType.NUMBER).description("총 데이터수"),
                        fieldWithPath("data.number_of_elements").type(JsonFieldType.NUMBER).description("페이지 총 데이터수"),
                        fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("페이지 마지막 데이터 여부"),
                        fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("페이지 데이터 존재 여부"),
                        fieldWithPath("data.list[]").type(JsonFieldType.ARRAY).description("등록 상품 list"),
                        fieldWithPath("data.list[].id").type(JsonFieldType.NUMBER).description("등록 상품 id"),
                        fieldWithPath("data.list[].main_title").type(JsonFieldType.STRING).description("메인 타이틀"),
                        fieldWithPath("data.list[].main_explanation").type(JsonFieldType.STRING).description("메인 설명"),
                        fieldWithPath("data.list[].product_main_explanation").type(JsonFieldType.STRING).description("상품 메인 설명"),
                        fieldWithPath("data.list[].product_sub_explanation").type(JsonFieldType.STRING).description("상품 서브 설명"),
                        fieldWithPath("data.list[].origin_price").type(JsonFieldType.NUMBER).description("상품 원가"),
                        fieldWithPath("data.list[].price").type(JsonFieldType.NUMBER).description("상품 실제 판매 가격"),
                        fieldWithPath("data.list[].purchase_inquiry").type(JsonFieldType.STRING).description("취급 방법"),
                        fieldWithPath("data.list[].origin").type(JsonFieldType.STRING).description("원산지"),
                        fieldWithPath("data.list[].producer").type(JsonFieldType.STRING).description("공급자"),
                        fieldWithPath("data.list[].main_image").type(JsonFieldType.STRING).description("메인 이미지 url"),
                        fieldWithPath("data.list[].image1").type(JsonFieldType.STRING).description("이미지1"),
                        fieldWithPath("data.list[].image2").type(JsonFieldType.STRING).description("이미지2"),
                        fieldWithPath("data.list[].image3").type(JsonFieldType.STRING).description("이미지3"),
                        fieldWithPath("data.list[].create_at").type(JsonFieldType.STRING).description("등록일"),
                        fieldWithPath("data.list[].modified_at").type(JsonFieldType.STRING).description("수정일")
                )
        ));
    }
}