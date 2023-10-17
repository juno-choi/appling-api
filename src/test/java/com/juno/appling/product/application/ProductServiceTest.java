package com.juno.appling.product.application;

import com.juno.appling.global.base.MessageVo;
import com.juno.appling.member.application.MemberAuthService;
import com.juno.appling.member.domain.Member;
import com.juno.appling.member.domain.MemberRepository;
import com.juno.appling.member.domain.Seller;
import com.juno.appling.member.domain.SellerRepository;
import com.juno.appling.product.domain.Category;
import com.juno.appling.product.domain.CategoryRepository;
import com.juno.appling.product.domain.Product;
import com.juno.appling.product.domain.ProductRepository;
import com.juno.appling.product.dto.request.AddViewCntRequest;
import com.juno.appling.product.dto.request.ProductRequest;
import com.juno.appling.product.dto.request.PutProductRequest;
import com.juno.appling.product.dto.response.ProductListResponse;
import com.juno.appling.product.dto.response.ProductResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.juno.appling.Base.SELLER_LOGIN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
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

    @Test
    @DisplayName("상품 등록 성공")
    void postProductSuccess() {
        //given
        request.removeHeader(AUTHORIZATION);
        request.addHeader(AUTHORIZATION, "Bearer " + SELLER_LOGIN.getAccessToken());

        ProductRequest productRequest = new ProductRequest(1L, "메인 타이틀", "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000,
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
        Member member = memberRepository.findByEmail("seller@appling.com").get();
        Long categoryId = 1L;
        ProductRequest productRequest = new ProductRequest(categoryId, "메인 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000, 8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal", 10, null, "normal");
        ProductRequest searchDto = new ProductRequest(categoryId, "검색 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000, 8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal", 10, null, "normal");
        Category category = categoryRepository.findById(categoryId).get();

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
            categoryId, 0L);
        //then
        assertThat(searchList.getList().stream().findFirst().get().getMainTitle()).contains("검색");
    }

    @Test
    @DisplayName("상품 리스트 판매자 계정 조건으로 불러오기")
    void getProductListSuccess2() {
        //given
        Member member = memberRepository.findByEmail("seller@appling.com").get();
        Member member2 = memberRepository.findByEmail("seller2@appling.com").get();
        Long categoryId = 1L;

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
        Member member = memberRepository.findByEmail("seller@appling.com").get();
        Category category = categoryRepository.findById(1L).get();

        ProductRequest productRequest = new ProductRequest(1L, "메인 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal", 10, null, "normal");
        Seller seller = sellerRepository.findByMember(member).get();

        Product originalProduct = productRepository.save(Product.of(seller, category, productRequest));
        String originalProductMainTitle = originalProduct.getMainTitle();
        Long productId = originalProduct.getId();
        PutProductRequest putProductRequest = PutProductRequest.builder()
                .productId(productId)
                .categoryId(2L)
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
        Member member = memberRepository.findByEmail("seller@appling.com").get();
        Category category = categoryRepository.findById(1L).get();
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