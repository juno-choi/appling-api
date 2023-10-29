package com.juno.appling.order.controller;

import com.juno.appling.RestdocsBaseTest;
import com.juno.appling.member.controller.request.LoginRequest;
import com.juno.appling.member.controller.response.LoginResponse;
import com.juno.appling.member.domain.Member;
import com.juno.appling.member.infrastruceture.MemberRepository;
import com.juno.appling.member.infrastruceture.SellerRepository;
import com.juno.appling.member.service.MemberAuthService;
import com.juno.appling.order.controller.request.CompleteOrderRequest;
import com.juno.appling.order.controller.request.TempOrderDto;
import com.juno.appling.order.controller.request.TempOrderRequest;
import com.juno.appling.order.domain.Order;
import com.juno.appling.order.domain.OrderItem;
import com.juno.appling.order.infrastructure.DeliveryRepository;
import com.juno.appling.order.infrastructure.OrderItemRepository;
import com.juno.appling.order.infrastructure.OrderRepository;
import com.juno.appling.product.domain.Option;
import com.juno.appling.product.domain.Product;
import com.juno.appling.product.infrastructure.CategoryRepository;
import com.juno.appling.product.infrastructure.OptionRepository;
import com.juno.appling.product.infrastructure.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static com.juno.appling.Base.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
@SqlGroup({
    @Sql(scripts = {"/sql/init.sql", "/sql/product.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD),
})
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
@Execution(ExecutionMode.CONCURRENT)
class OrderControllerDocs extends RestdocsBaseTest {

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

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    private ObjectMapper objectMapper = new ObjectMapper();


    private final static String PREFIX = "/api/order";

    @AfterEach
    void cleanup() {
        deliveryRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        optionRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        sellerRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName(PREFIX + " (POST)")
    void postTempOrder() throws Exception {
        //given
        LoginRequest loginRequest = new LoginRequest(MEMBER_EMAIL, "password");
        LoginResponse login = memberAuthService.login(loginRequest);

        List<TempOrderDto> orderList = new ArrayList<>();
        int orderEa1 = 3;
        int orderEa2 = 2;
        TempOrderDto order1 = TempOrderDto.builder()
            .productId(PRODUCT_ID_APPLE)
            .ea(orderEa1)
            .optionId(PRODUCT_OPTION_ID_APPLE)
            .build();
        TempOrderDto order2 = TempOrderDto.builder()
            .productId(PRODUCT_ID_PEAR)
            .ea(orderEa2)
            .optionId(PRODUCT_OPTION_ID_PEAR)
            .build();
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
                        fieldWithPath("order_list[].ea").type(JsonFieldType.NUMBER).description("주문 개수"),
                        fieldWithPath("order_list[].option_id").type(JsonFieldType.NUMBER).description("상품 옵션 id").optional()
                ),
                responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                        fieldWithPath("data.order_id").type(JsonFieldType.NUMBER).description("주문 id")
                )
        ));
    }

    @Test
    @DisplayName(PREFIX + "/temp/{order_id} (GET)")
    void getTempOrder() throws Exception {
        //given
        LoginRequest loginRequest = new LoginRequest(MEMBER_EMAIL, "password");
        LoginResponse login = memberAuthService.login(loginRequest);
        Member member = memberRepository.findByEmail(MEMBER_EMAIL).get();

        Order order = orderRepository.save(Order.of(member, "테스트 상품"));

        Product normalProduct = productRepository.findById(PRODUCT_ID_NORMAL).get();
        Product optionProduct = productRepository.findById(PRODUCT_ID_APPLE).get();

        Option option1 = optionRepository.findById(PRODUCT_OPTION_ID_APPLE).get();

        orderItemRepository.save(OrderItem.of(order, normalProduct, null, 3));
        orderItemRepository.save(OrderItem.of(order, optionProduct, option1, 5));

        //when
        ResultActions perform = mock.perform(
                get(PREFIX + "/temp/{order_id}", order.getId())
                        .header(AUTHORIZATION, "Bearer " + login.getAccessToken())
        );
        //then
        perform.andExpect(status().is2xxSuccessful());
        perform.andDo(docs.document(
                requestHeaders(
                        headerWithName(AUTHORIZATION).description("access token (MEMBER 권한 이상)")
                ),
                pathParameters(
                        parameterWithName("order_id").description("주문 id")
                ),
                responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                        fieldWithPath("data.order_id").type(JsonFieldType.NUMBER).description("주문 id"),
                        fieldWithPath("data.order_item_list").type(JsonFieldType.ARRAY).description("주문 상품 리스트"),
                        fieldWithPath("data.order_item_list[].product_id").type(JsonFieldType.NUMBER).description("상품 id"),
                        fieldWithPath("data.order_item_list[].ea").type(JsonFieldType.NUMBER).description("상품 주문 개수"),
                        fieldWithPath("data.order_item_list[].main_title").type(JsonFieldType.STRING).description("주문 상품명"),
                        fieldWithPath("data.order_item_list[].main_explanation").type(JsonFieldType.STRING).description("주문 상품 메인 설명"),
                        fieldWithPath("data.order_item_list[].product_main_explanation").type(JsonFieldType.STRING).description("주문 상품 상세 설명"),
                        fieldWithPath("data.order_item_list[].product_sub_explanation").type(JsonFieldType.STRING).description("주문 상품 상세 서브 설명"),
                        fieldWithPath("data.order_item_list[].origin_price").type(JsonFieldType.NUMBER).description("주문 상품 원가"),
                        fieldWithPath("data.order_item_list[].price").type(JsonFieldType.NUMBER).description("주문 상품 판매가"),
                        fieldWithPath("data.order_item_list[].purchase_inquiry").type(JsonFieldType.STRING).description("주문 상품 취급 주의 사항"),
                        fieldWithPath("data.order_item_list[].origin").type(JsonFieldType.STRING).description("원산지"),
                        fieldWithPath("data.order_item_list[].producer").type(JsonFieldType.STRING).description("공급자"),
                        fieldWithPath("data.order_item_list[].main_image").type(JsonFieldType.STRING).description("메인 이미지"),
                        fieldWithPath("data.order_item_list[].image1").type(JsonFieldType.STRING).description("이미지1"),
                        fieldWithPath("data.order_item_list[].image2").type(JsonFieldType.STRING).description("이미지2"),
                        fieldWithPath("data.order_item_list[].image3").type(JsonFieldType.STRING).description("이미지3"),
                        fieldWithPath("data.order_item_list[].view_cnt").type(JsonFieldType.NUMBER).description("조회수"),
                        fieldWithPath("data.order_item_list[].status").type(JsonFieldType.STRING).description("상품 상태"),
                        fieldWithPath("data.order_item_list[].created_at").type(JsonFieldType.STRING).description("상품 생성일"),
                        fieldWithPath("data.order_item_list[].modified_at").type(JsonFieldType.STRING).description("상품 수정일"),
                        fieldWithPath("data.order_item_list[].seller").type(JsonFieldType.OBJECT).description("상품 판매자"),
                        fieldWithPath("data.order_item_list[].seller.seller_id").type(JsonFieldType.NUMBER).description("상품 판매자 id"),
                        fieldWithPath("data.order_item_list[].seller.email").type(JsonFieldType.STRING).description("상품 판매자 email"),
                        fieldWithPath("data.order_item_list[].seller.company").type(JsonFieldType.STRING).description("상품 판매자 회사명"),
                        fieldWithPath("data.order_item_list[].seller.zonecode").type(JsonFieldType.STRING).description("상품 판매자 우편 주소"),
                        fieldWithPath("data.order_item_list[].seller.address").type(JsonFieldType.STRING).description("상품 판매자 주소"),
                        fieldWithPath("data.order_item_list[].seller.address_detail").type(JsonFieldType.STRING).description("상품 판매자 상세 주소"),
                        fieldWithPath("data.order_item_list[].seller.tel").type(JsonFieldType.STRING).description("상품 판매자 연락처"),
                        fieldWithPath("data.order_item_list[].category").type(JsonFieldType.OBJECT).description("상품 카테고리"),
                        fieldWithPath("data.order_item_list[].category.category_id").type(JsonFieldType.NUMBER).description("상품 카테고리 id"),
                        fieldWithPath("data.order_item_list[].category.name").type(JsonFieldType.STRING).description("상품 카테고리명"),
                        fieldWithPath("data.order_item_list[].category.created_at").type(JsonFieldType.STRING).description("상품 카테고리 생성일"),
                        fieldWithPath("data.order_item_list[].category.modified_at").type(JsonFieldType.STRING).description("상품 카테고리 수정일"),
                        fieldWithPath("data.order_item_list[].option").type(JsonFieldType.OBJECT).description("상품 옵션").optional(),
                        fieldWithPath("data.order_item_list[].option.option_id").type(JsonFieldType.NUMBER).description("상품 옵션 id").optional(),
                        fieldWithPath("data.order_item_list[].option.name").type(JsonFieldType.STRING).description("상품 옵션명").optional(),
                        fieldWithPath("data.order_item_list[].option.extra_price").type(JsonFieldType.NUMBER).description("상품 옵션 추가 금액").optional(),
                        fieldWithPath("data.order_item_list[].option.ea").type(JsonFieldType.NUMBER).description("상품 옵션 재고").optional(),
                        fieldWithPath("data.order_item_list[].option.created_at").type(JsonFieldType.STRING).description("상품 옵션 생성일").optional(),
                        fieldWithPath("data.order_item_list[].option.modified_at").type(JsonFieldType.STRING).description("상품 옵션 수정일").optional()
                )
        ));
    }

    @Test
    @DisplayName(PREFIX + "/complete (PATCH)")
    void complete() throws Exception {
        //given
        LoginRequest loginRequest = new LoginRequest(MEMBER_EMAIL, "password");
        LoginResponse login = memberAuthService.login(loginRequest);
        Member member = memberRepository.findByEmail(MEMBER_EMAIL).get();

        Order order = orderRepository.save(Order.of(member, "테스트 상품"));
        Long orderId = order.getId();

        Product product1 = productRepository.findById(PRODUCT_ID_APPLE).get();
        Product product2 = productRepository.findById(PRODUCT_ID_NORMAL).get();

        Option option1 = optionRepository.findById(PRODUCT_OPTION_ID_APPLE).get();

        orderItemRepository.save(OrderItem.of(order, product1, option1, 3));
        orderItemRepository.save(OrderItem.of(order, product2, null, 5));

        CompleteOrderRequest completeOrderRequest = new CompleteOrderRequest(orderId, "주문자", "주문자 우편번호", "주문자 주소", "주문자 상세 주소", "주문자 연락처", "수령인", "수령인 우편번호", "수령인 주소", "수령인 상세 주소", "수령인 연락처");
        //when
        ResultActions perform = mock.perform(
                patch(PREFIX + "/complete")
                        .header(AUTHORIZATION, "Bearer " + login.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(completeOrderRequest))
        );
        //then
        perform.andDo(docs.document(
                requestHeaders(
                        headerWithName(AUTHORIZATION).description("access token (MEMBER 권한 이상)")
                ),
                requestFields(
                        fieldWithPath("order_id").description("주문 id"),
                        fieldWithPath("owner_name").description("주문자"),
                        fieldWithPath("owner_zonecode").description("주문자 우편번호"),
                        fieldWithPath("owner_address").description("주문자 주소"),
                        fieldWithPath("owner_address_detail").description("주문자 상세 주소"),
                        fieldWithPath("owner_tel").description("주문자 연락처"),
                        fieldWithPath("recipient_name").description("수령인"),
                        fieldWithPath("recipient_zonecode").description("수령인 우편번호"),
                        fieldWithPath("recipient_address").description("수령인 주소"),
                        fieldWithPath("recipient_address_detail").description("수령인 상세 주소"),
                        fieldWithPath("recipient_tel").description("수령인 연락처")
                ),
                responseFields(
                        fieldWithPath("code").type(JsonFieldType.STRING).description("결과 코드"),
                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지"),
                        fieldWithPath("data.order_id").type(JsonFieldType.NUMBER).description("주문 id"),
                        fieldWithPath("data.order_number").type(JsonFieldType.STRING).description("주문 번호")
                )
        ));
    }

}