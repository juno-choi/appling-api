package com.juno.appling.service.product;

import com.juno.appling.domain.dto.member.LoginDto;
import com.juno.appling.domain.dto.product.PutProductDto;
import com.juno.appling.domain.dto.product.ProductDto;
import com.juno.appling.domain.entity.member.Member;
import com.juno.appling.domain.entity.product.Product;
import com.juno.appling.domain.vo.member.LoginVo;
import com.juno.appling.domain.vo.product.ProductListVo;
import com.juno.appling.domain.vo.product.ProductVo;
import com.juno.appling.repository.member.MemberRepository;
import com.juno.appling.repository.product.ProductRepository;
import com.juno.appling.service.member.MemberAuthService;
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

@SpringBootTest
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

    private MockHttpServletRequest request = new MockHttpServletRequest();

    @Test
    @DisplayName("상품 등록 성공")
    void postProduct() {
        //given
        LoginDto loginDto = new LoginDto("seller@appling.com", "password");
        LoginVo login = memberAuthService.login(loginDto);
        request.addHeader(AUTHORIZATION, "Bearer "+login.getAccessToken());

        ProductDto productDto = new ProductDto("메인 타이틀", "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000, 9000, "취급 방법", "원산지", "공급자", "https://메인이미지", "https://image1", "https://image2", "https://image3");

        //when
        ProductVo productVo = productService.postProduct(productDto, request);

        //then
        Optional<Product> byId = productRepository.findById(productVo.getId());
        Product product = byId.get();
        String email = product.getMember().getEmail();

        assertThat(byId).isNotEmpty();
        assertThat(email).isEqualTo(loginDto.getEmail());
    }

    @Test
    @DisplayName("상품 리스트 불러오기")
    void getProductList() {
        //given
        Member member = memberRepository.findByEmail("seller@appling.com").get();

        ProductDto productDto = new ProductDto("메인 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000, 8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null);
        ProductDto searchDto = new ProductDto("검색 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000, 8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null);
        productRepository.save(Product.of(member, searchDto));

        for(int i=0; i<25; i++){
            productRepository.save(Product.of(member, productDto));
        }

        for(int i=0; i<10; i++){
            productRepository.save(Product.of(member, searchDto));
        }

        Pageable pageable = Pageable.ofSize(5);
        pageable = pageable.next();
        //when
        ProductListVo searchList = productService.getProductList(pageable, "검색");
        //then
        assertThat(searchList.getList().stream().findFirst().get().getMainTitle()).contains("검색");
    }

    @Test
    @DisplayName("상품 수정 성공")
    void putProductSuccess(){
        // given
        Member member = memberRepository.findByEmail("seller@appling.com").get();
        ProductDto productDto = new ProductDto("메인 제목", "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000, 8000, "보관 방법", "원산지", "생산자", "https://mainImage", null, null, null);
        Product originalProduct = productRepository.save(Product.of(member, productDto));
        String originalProductMainTitle = originalProduct.getMainTitle();
        Long productId = originalProduct.getId();
        PutProductDto putProductDto = new PutProductDto(productId, "수정된 제목", "수정된 설명", "상품 메인 설명", "상품 서브 설명", 12000, 10000, "보관 방법", "원산지", "생산자", "https://mainImage", "https://image1", "https://image2", "https://image3");
        // when
        productService.putProduct(putProductDto);
        // then
        Product changeProduct = productRepository.findById(productId).get();
        assertThat(changeProduct.getMainTitle()).isNotEqualTo(originalProductMainTitle);
    }
}