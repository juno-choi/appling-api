package com.juno.appling.product.service;

import com.juno.appling.global.base.MessageVo;
import com.juno.appling.member.service.MemberAuthService;
import com.juno.appling.member.domain.Member;
import com.juno.appling.member.infrastruceture.MemberRepository;
import com.juno.appling.member.domain.Seller;
import com.juno.appling.member.infrastruceture.SellerRepository;
import com.juno.appling.product.domain.Category;
import com.juno.appling.product.infrastructure.CategoryRepository;
import com.juno.appling.product.domain.Product;
import com.juno.appling.product.infrastructure.ProductRepository;
import com.juno.appling.product.controller.request.AddViewCntRequest;
import com.juno.appling.product.controller.request.ProductRequest;
import com.juno.appling.product.controller.request.PutProductRequest;
import com.juno.appling.product.controller.response.ProductListResponse;
import com.juno.appling.product.controller.response.ProductResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.juno.appling.Base.CATEGORY_ID_FRUIT;
import static com.juno.appling.Base.CATEGORY_ID_VEGETABLE;
import static com.juno.appling.Base.SELLER2_EMAIL;
import static com.juno.appling.Base.SELLER_EMAIL;
import static com.juno.appling.Base.SELLER_LOGIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SqlGroup({
    @Sql(scripts = {"/sql/init.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
})
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
@Transactional(readOnly = true)
@Execution(ExecutionMode.CONCURRENT)
class ProductServiceTest {

    @Autowired
    private ProductService productService;
    @Autowired
    private MemberAuthService memberAuthService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private SellerRepository sellerRepository;

    private MockHttpServletRequest request = new MockHttpServletRequest();

    @AfterEach
    void cleanup() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        sellerRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("상품 등록 성공")
    void postProductSuccess() {
        //given
        request.removeHeader(AUTHORIZATION);
        request.addHeader(AUTHORIZATION, "Bearer " + SELLER_LOGIN.getAccessToken());

        ProductRequest productRequest = new ProductRequest(CATEGORY_ID_FRUIT, "메인 타이틀", "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000,
            9000, "취급 방법", "원산지", "공급자", "https://메인이미지", "https://image1", "https://image2",
            "https://image3", "normal", 10, null, "normal");

        //when
        ProductResponse productResponse = productService.postProduct(productRequest, request);

        //then
        Optional<Product> byId = productRepository.findById(productResponse.getProductId());
        Product product = byId.get();
        String email = product.getSeller().getEmail();

        assertThat(byId).isNotEmpty();
    }

    @Test
    @DisplayName("상품 리스트 불러오기")
    void getProductListSuccess() {
        //given
        Member member = memberRepository.findByEmail(SELLER_EMAIL).get();
        ProductRequest productRequest = new ProductRequest(CATEGORY_ID_FRUIT, "메인 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000, 8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal", 10, null, "normal");
        ProductRequest searchDto = new ProductRequest(CATEGORY_ID_FRUIT, "검색 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000, 8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal", 10, null, "normal");
        Category category = categoryRepository.findById(CATEGORY_ID_FRUIT).get();

        Seller seller = sellerRepository.findByMember(member).get();
        productRepository.save(Product.of(seller, category, searchDto));

        for (int i = 0; i < 25; i++) {
            productRepository.save(Product.of(seller, category, productRequest));
        }

        for (int i = 0; i < 10; i++) {
            productRepository.save(Product.of(seller, category, searchDto));
        }

        Pageable pageable = Pageable.ofSize(5);
        pageable = pageable.next();
        //when
        ProductListResponse searchList = productService.getProductList(pageable, "검색", "normal",
            CATEGORY_ID_FRUIT, 0L);
        //then
        assertThat(searchList.getList().stream().findFirst().get().getMainTitle()).contains("검색");
    }

    @Test
    @DisplayName("상품 리스트 판매자 계정 조건으로 불러오기")
    void getProductListSuccess2() {
        //given
        Member member = memberRepository.findByEmail(SELLER_EMAIL).get();
        Member member2 = memberRepository.findByEmail(SELLER2_EMAIL).get();
        Long categoryId = CATEGORY_ID_FRUIT;

        Category category = categoryRepository.findById(categoryId).get();

        ProductRequest productRequest = new ProductRequest(categoryId, "메인 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000, 8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal", 10, null, "normal");
        ProductRequest searchDto = new ProductRequest(categoryId, "검색 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000, 8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal", 10, null, "normal");

        Seller seller = sellerRepository.findByMember(member).get();
        Seller seller2 = sellerRepository.findByMember(member2).get();
        productRepository.save(Product.of(seller, category, searchDto));

        for (int i = 0; i < 10; i++) {
            productRepository.save(Product.of(seller, category, searchDto));
        }
        for (int i = 0; i < 10; i++) {
            productRepository.save(Product.of(seller2, category, productRequest));
        }

        Pageable pageable = Pageable.ofSize(5);
        pageable = pageable.next();
        request.removeHeader(AUTHORIZATION);
        request.addHeader(AUTHORIZATION, "Bearer " + SELLER_LOGIN.getAccessToken());
        //when
        ProductListResponse searchList = productService.getProductListBySeller(pageable, "", "normal",
            categoryId, request);
        //then
        assertThat(searchList.getList().stream().findFirst().get().getSeller().getSellerId()).isEqualTo(
            seller.getId());
    }

    @Test
    @DisplayName("상품 수정 성공")
    void putProductSuccess() {
        // given
        Member member = memberRepository.findByEmail(SELLER_EMAIL).get();
        Category category = categoryRepository.findById(CATEGORY_ID_FRUIT).get();

        ProductRequest productRequest = new ProductRequest(CATEGORY_ID_FRUIT, "메인 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal", 10, null, "normal");
        Seller seller = sellerRepository.findByMember(member).get();

        Product originalProduct = productRepository.save(Product.of(seller, category, productRequest));
        String originalProductMainTitle = originalProduct.getMainTitle();
        Long productId = originalProduct.getId();
        PutProductRequest putProductRequest = PutProductRequest.builder()
                .productId(productId)
                .categoryId(CATEGORY_ID_VEGETABLE)
                .mainTitle("수정된 제목")
                .mainExplanation("수정된 설명")
                .mainImage("https://mainImage")
                .origin("원산지")
                .purchaseInquiry("보관방법")
                .producer("생산자")
                .originPrice(12000)
                .price(10000)
                .image1("https://image1")
                .image2("https://image2")
                .image3("https://image3")
                .status("normal")
                .ea(10)
                .build();
        // when
        productService.putProduct(putProductRequest);
        // then
        Product changeProduct = productRepository.findById(productId).get();
        assertThat(changeProduct.getMainTitle()).isNotEqualTo(originalProductMainTitle);
    }

    @Test
    @DisplayName("상품 조회수 증가 성공")
    void addViewCntSuccess() {
        //given
        Member member = memberRepository.findByEmail(SELLER_EMAIL).get();
        Category category = categoryRepository.findById(CATEGORY_ID_FRUIT).get();
        ProductRequest productRequest = new ProductRequest(1L, "메인 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal", 10, null, "normal");

        Seller seller = sellerRepository.findByMember(member).get();
        Product product = productRepository.save(Product.of(seller, category, productRequest));
        //when
        MessageVo messageVo = productService.addViewCnt(new AddViewCntRequest(product.getId()));

        //then
        assertThat(messageVo.message())
            .contains("조회수 증가 성공");
        assertThat(product.getViewCnt())
            .isEqualTo(1);
    }
}