package com.juno.appling.product.service;

import com.juno.appling.BaseTest;
import com.juno.appling.global.base.MessageVo;
import com.juno.appling.member.domain.dto.LoginDto;
import com.juno.appling.member.domain.entity.Member;
import com.juno.appling.member.domain.entity.Seller;
import com.juno.appling.member.domain.vo.LoginVo;
import com.juno.appling.member.repository.MemberRepository;
import com.juno.appling.member.repository.SellerRepository;
import com.juno.appling.member.service.MemberAuthService;
import com.juno.appling.product.domain.dto.AddViewCntDto;
import com.juno.appling.product.domain.dto.ProductDto;
import com.juno.appling.product.domain.dto.PutProductDto;
import com.juno.appling.product.domain.entity.Category;
import com.juno.appling.product.domain.entity.Product;
import com.juno.appling.product.domain.vo.ProductListVo;
import com.juno.appling.product.domain.vo.ProductVo;
import com.juno.appling.product.repository.CategoryRepository;
import com.juno.appling.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
class ProductServiceTest extends BaseTest {

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
        LoginDto loginDto = new LoginDto("seller@appling.com", "password");
        LoginVo login = memberAuthService.login(loginDto);
        request.removeHeader(AUTHORIZATION);
        request.addHeader(AUTHORIZATION, "Bearer " + login.accessToken());

        ProductDto productDto = new ProductDto(1L, "메인 타이틀", "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000,
            9000, "취급 방법", "원산지", "공급자", "https://메인이미지", "https://image1", "https://image2",
            "https://image3", "normal");

        //when
        ProductVo productVo = productService.postProduct(productDto, request);

        //then
        Optional<Product> byId = productRepository.findById(productVo.id());
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
        ProductDto productDto = new ProductDto(categoryId, "메인 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000, 8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal");
        ProductDto searchDto = new ProductDto(categoryId, "검색 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000, 8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal");
        Category category = categoryRepository.findById(categoryId).get();

        Seller seller = sellerRepository.findByMember(member).get();
        productRepository.save(Product.of(seller, category, searchDto));

        for (int i = 0; i < 25; i++) {
            productRepository.save(Product.of(seller, category, productDto));
        }

        for (int i = 0; i < 10; i++) {
            productRepository.save(Product.of(seller, category, searchDto));
        }

        Pageable pageable = Pageable.ofSize(5);
        pageable = pageable.next();
        //when
        ProductListVo searchList = productService.getProductList(pageable, "검색", "normal",
            categoryId, 0L);
        //then
        assertThat(searchList.list().stream().findFirst().get().mainTitle()).contains("검색");
    }

    @Test
    @DisplayName("상품 리스트 판매자 계정 조건으로 불러오기")
    void getProductListSuccess2() {
        //given
        Member member = memberRepository.findByEmail("seller@appling.com").get();
        Member member2 = memberRepository.findByEmail("seller2@appling.com").get();
        LoginDto loginDto = new LoginDto("seller@appling.com", "password");
        LoginVo login = memberAuthService.login(loginDto);
        Long categoryId = 1L;

        Category category = categoryRepository.findById(categoryId).get();

        ProductDto productDto = new ProductDto(categoryId, "메인 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000, 8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal");
        ProductDto searchDto = new ProductDto(categoryId, "검색 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000, 8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal");

        Seller seller = sellerRepository.findByMember(member).get();
        Seller seller2 = sellerRepository.findByMember(member2).get();
        productRepository.save(Product.of(seller, category, searchDto));

        for (int i = 0; i < 10; i++) {
            productRepository.save(Product.of(seller, category, searchDto));
        }
        for (int i = 0; i < 10; i++) {
            productRepository.save(Product.of(seller2, category, productDto));
        }

        Pageable pageable = Pageable.ofSize(5);
        pageable = pageable.next();
        request.removeHeader(AUTHORIZATION);
        request.addHeader(AUTHORIZATION, "Bearer " + login.accessToken());
        //when
        ProductListVo searchList = productService.getProductListBySeller(pageable, "", "normal",
            categoryId, request);
        //then
        assertThat(searchList.list().stream().findFirst().get().seller().sellerId()).isEqualTo(
            seller.getId());
    }

    @Test
    @DisplayName("상품 수정 성공")
    void putProductSuccess() {
        // given
        Member member = memberRepository.findByEmail("seller@appling.com").get();
        Category category = categoryRepository.findById(1L).get();

        ProductDto productDto = new ProductDto(1L, "메인 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal");
        Seller seller = sellerRepository.findByMember(member).get();

        Product originalProduct = productRepository.save(Product.of(seller, category, productDto));
        String originalProductMainTitle = originalProduct.getMainTitle();
        Long productId = originalProduct.getId();
        Long categoryId = originalProduct.getCategory().getId();
        PutProductDto putProductDto = new PutProductDto(productId, 2L, "수정된 제목", "수정된 설명",
            "상품 메인 설명", "상품 서브 설명", 12000, 10000, "보관 방법", "원산지", "생산자", "https://mainImage",
            "https://image1", "https://image2", "https://image3", "normal");
        // when
        productService.putProduct(putProductDto);
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
        ProductDto productDto = new ProductDto(1L, "메인 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000,
            8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null, "normal");

        Seller seller = sellerRepository.findByMember(member).get();

        Product product = productRepository.save(Product.of(seller, category, productDto));
        //when
        MessageVo messageVo = productService.addViewCnt(new AddViewCntDto(product.getId()));

        //then
        assertThat(messageVo.message())
            .contains("조회수 증가 성공");
        assertThat(product.getViewCnt())
            .isEqualTo(1);
    }
}