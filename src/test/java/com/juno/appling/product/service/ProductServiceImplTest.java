package com.juno.appling.product.service;

import com.juno.appling.global.base.MessageVo;
import com.juno.appling.member.domain.entity.MemberEntity;
import com.juno.appling.member.repository.MemberJpaRepository;
import com.juno.appling.member.service.MemberAuthService;
import com.juno.appling.product.controller.request.AddViewCntRequest;
import com.juno.appling.product.controller.request.ProductRequest;
import com.juno.appling.product.controller.request.PutProductRequest;
import com.juno.appling.product.controller.response.ProductListResponse;
import com.juno.appling.product.controller.response.ProductResponse;
import com.juno.appling.product.domain.entity.CategoryEntity;
import com.juno.appling.product.domain.entity.ProductEntity;
import com.juno.appling.product.domain.entity.SellerEntity;
import com.juno.appling.product.repository.CategoryJpaRepository;
import com.juno.appling.product.repository.ProductJpaRepository;
import com.juno.appling.product.repository.SellerJpaRepository;
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

import static com.juno.appling.Base.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SqlGroup({
    @Sql(scripts = {"/sql/init.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
})
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
@Transactional(readOnly = true)
@Execution(ExecutionMode.CONCURRENT)
class ProductServiceImplTest {

    @Autowired
    private ProductService productService;
    @Autowired
    private MemberAuthService memberAuthService;
    @Autowired
    private MemberJpaRepository memberJpaRepository;
    @Autowired
    private ProductJpaRepository productJpaRepository;
    @Autowired
    private CategoryJpaRepository categoryJpaRepository;
    @Autowired
    private SellerJpaRepository sellerJpaRepository;

    private MockHttpServletRequest request = new MockHttpServletRequest();

    @AfterEach
    void cleanup() {
        productJpaRepository.deleteAll();
        categoryJpaRepository.deleteAll();
        sellerJpaRepository.deleteAll();
        memberJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("상품 등록 성공")
    void postProductSuccess() {
        //given
        request.removeHeader(AUTHORIZATION);
        request.addHeader(AUTHORIZATION, "Bearer " + SELLER_LOGIN.getAccessToken());

        ProductRequest productRequest = ProductRequest.builder()
            .categoryId(CATEGORY_ID_FRUIT)
            .mainTitle("메인 제목")
            .mainExplanation("메인 설명")
            .productMainExplanation("상품 메인 설명")
            .productSubExplanation("상품 서브 설명")
            .originPrice(10000)
            .price(8000)
            .purchaseInquiry("보관 방법")
            .origin("원산지")
            .producer("생산자")
            .mainImage("https://mainImage")
            .image1("https://image1")
            .image2("https://image2")
            .image3("https://image3")
            .status("normal")
            .ea(10)
            .type("normal")
            .build();

        //when
        ProductResponse productResponse = productService.postProduct(productRequest, request);

        //then
        Optional<ProductEntity> byId = productJpaRepository.findById(productResponse.getProductId());
        ProductEntity productEntity = byId.get();
        String email = productEntity.getSeller().getEmail();

        assertThat(byId).isNotEmpty();
    }

    @Test
    @DisplayName("상품 리스트 불러오기")
    void getProductListSuccess() {
        //given
        String mainTitle = "검색";
        MemberEntity memberEntity = memberJpaRepository.findByEmail(SELLER_EMAIL).get();
        ProductRequest productRequest = ProductRequest.builder()
            .categoryId(CATEGORY_ID_FRUIT)
            .mainTitle(mainTitle)
            .mainExplanation("메인 설명")
            .productMainExplanation("상품 메인 설명")
            .productSubExplanation("상품 서브 설명")
            .originPrice(10000)
            .price(8000)
            .purchaseInquiry("보관 방법")
            .origin("원산지")
            .producer("생산자")
            .mainImage("https://mainImage")
            .image1("https://image1")
            .image2("https://image2")
            .image3("https://image3")
            .status("normal")
            .ea(10)
            .type("normal")
            .build();
        ProductRequest searchDto = ProductRequest.builder()
            .categoryId(CATEGORY_ID_FRUIT)
            .mainTitle(mainTitle+"2")
            .mainExplanation("메인 설명")
            .productMainExplanation("상품 메인 설명")
            .productSubExplanation("상품 서브 설명")
            .originPrice(10000)
            .price(8000)
            .purchaseInquiry("보관 방법")
            .origin("원산지")
            .producer("생산자")
            .mainImage("https://mainImage")
            .image1("https://image1")
            .image2("https://image2")
            .image3("https://image3")
            .status("normal")
            .ea(10)
            .type("normal")
            .build();
        CategoryEntity categoryEntity = categoryJpaRepository.findById(CATEGORY_ID_FRUIT).get();

        SellerEntity sellerEntity = sellerJpaRepository.findByMember(memberEntity).get();
        productJpaRepository.save(ProductEntity.of(sellerEntity, categoryEntity, searchDto));

        for (int i = 0; i < 25; i++) {
            productJpaRepository.save(ProductEntity.of(sellerEntity, categoryEntity, productRequest));
        }

        for (int i = 0; i < 10; i++) {
            productJpaRepository.save(ProductEntity.of(sellerEntity, categoryEntity, searchDto));
        }

        Pageable pageable = Pageable.ofSize(5);
        pageable = pageable.next();
        //when
        ProductListResponse searchList = productService.getProductList(pageable, mainTitle, "normal",
            CATEGORY_ID_FRUIT, 0L);
        //then
        assertThat(searchList.getList().stream().findFirst().get().getMainTitle()).contains(mainTitle);
    }

    @Test
    @DisplayName("상품 리스트 판매자 계정 조건으로 불러오기")
    void getProductListSuccess2() {
        //given
        MemberEntity memberEntity = memberJpaRepository.findByEmail(SELLER_EMAIL).get();
        MemberEntity memberEntity2 = memberJpaRepository.findByEmail(SELLER2_EMAIL).get();
        Long categoryId = CATEGORY_ID_FRUIT;

        CategoryEntity categoryEntity = categoryJpaRepository.findById(categoryId).get();

        ProductRequest productRequest = ProductRequest.builder()
            .categoryId(categoryId)
            .mainTitle("메인 제목")
            .mainExplanation("메인 설명")
            .productMainExplanation("상품 메인 설명")
            .productSubExplanation("상품 서브 설명")
            .originPrice(10000)
            .price(8000)
            .purchaseInquiry("보관 방법")
            .origin("원산지")
            .producer("생산자")
            .mainImage("https://mainImage")
            .image1("https://image1")
            .image2("https://image2")
            .image3("https://image3")
            .status("normal")
            .ea(10)
            .type("normal")
            .build();
        ProductRequest searchDto = ProductRequest.builder()
            .categoryId(categoryId)
            .mainTitle("메인 제목")
            .mainExplanation("메인 설명")
            .productMainExplanation("상품 메인 설명")
            .productSubExplanation("상품 서브 설명")
            .originPrice(10000)
            .price(8000)
            .purchaseInquiry("보관 방법")
            .origin("원산지")
            .producer("생산자")
            .mainImage("https://mainImage")
            .image1("https://image1")
            .image2("https://image2")
            .image3("https://image3")
            .status("normal")
            .ea(10)
            .type("normal")
            .build();

        SellerEntity sellerEntity = sellerJpaRepository.findByMember(memberEntity).get();
        SellerEntity sellerEntity2 = sellerJpaRepository.findByMember(memberEntity2).get();
        productJpaRepository.save(ProductEntity.of(sellerEntity, categoryEntity, searchDto));

        for (int i = 0; i < 10; i++) {
            productJpaRepository.save(ProductEntity.of(sellerEntity, categoryEntity, searchDto));
        }
        for (int i = 0; i < 10; i++) {
            productJpaRepository.save(ProductEntity.of(sellerEntity2, categoryEntity, productRequest));
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
            sellerEntity.getId());
    }

    @Test
    @DisplayName("상품 수정 성공")
    void putProductSuccess() {
        // given
        MemberEntity memberEntity = memberJpaRepository.findByEmail(SELLER_EMAIL).get();
        CategoryEntity categoryEntity = categoryJpaRepository.findById(CATEGORY_ID_FRUIT).get();

        ProductRequest productRequest = ProductRequest.builder()
            .categoryId(CATEGORY_ID_FRUIT)
            .mainTitle("메인 제목")
            .mainExplanation("메인 설명")
            .productMainExplanation("상품 메인 설명")
            .productSubExplanation("상품 서브 설명")
            .originPrice(10000)
            .price(8000)
            .purchaseInquiry("보관 방법")
            .origin("원산지")
            .producer("생산자")
            .mainImage("https://mainImage")
            .image1("https://image1")
            .image2("https://image2")
            .image3("https://image3")
            .status("normal")
            .ea(10)
            .type("normal")
            .build();
        SellerEntity sellerEntity = sellerJpaRepository.findByMember(memberEntity).get();

        ProductEntity originalProductEntity = productJpaRepository.save(
            ProductEntity.of(sellerEntity, categoryEntity, productRequest));
        String originalProductMainTitle = originalProductEntity.getMainTitle();
        Long productId = originalProductEntity.getId();
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
        ProductEntity changeProductEntity = productJpaRepository.findById(productId).get();
        assertThat(changeProductEntity.getMainTitle()).isNotEqualTo(originalProductMainTitle);
    }

    @Test
    @DisplayName("상품 조회수 증가 성공")
    void addViewCntSuccess() {
        //given
        MemberEntity memberEntity = memberJpaRepository.findByEmail(SELLER_EMAIL).get();
        CategoryEntity categoryEntity = categoryJpaRepository.findById(CATEGORY_ID_FRUIT).get();
        ProductRequest productRequest = ProductRequest.builder()
            .categoryId(CATEGORY_ID_FRUIT)
            .mainTitle("메인 제목")
            .mainExplanation("메인 설명")
            .productMainExplanation("상품 메인 설명")
            .productSubExplanation("상품 서브 설명")
            .originPrice(10000)
            .price(8000)
            .purchaseInquiry("보관 방법")
            .origin("원산지")
            .producer("생산자")
            .mainImage("https://mainImage")
            .image1("https://image1")
            .image2("https://image2")
            .image3("https://image3")
            .status("normal")
            .ea(10)
            .type("normal")
            .build();

        SellerEntity sellerEntity = sellerJpaRepository.findByMember(memberEntity).get();
        ProductEntity productEntity = productJpaRepository.save(
            ProductEntity.of(sellerEntity, categoryEntity, productRequest));
        //when
        MessageVo messageVo = productService.addViewCnt(AddViewCntRequest.builder()
                .productId(productEntity.getId()).build());

        //then
        assertThat(messageVo.getMessage())
            .contains("조회수 증가 성공");
        assertThat(productEntity.getViewCnt())
            .isEqualTo(1);
    }
}