package com.juno.appling.domain.product.service;

import com.juno.appling.config.security.TokenProvider;
import com.juno.appling.domain.product.dto.PutProductDto;
import com.juno.appling.domain.product.dto.ProductDto;
import com.juno.appling.domain.member.entity.Member;
import com.juno.appling.domain.product.entity.Category;
import com.juno.appling.domain.product.entity.Product;
import com.juno.appling.domain.member.enums.Role;
import com.juno.appling.domain.product.service.ProductService;
import com.juno.appling.domain.product.vo.ProductVo;
import com.juno.appling.domain.member.repository.MemberRepository;
import com.juno.appling.domain.product.repository.CategoryRepository;
import com.juno.appling.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@ExtendWith(MockitoExtension.class)
class ProductServiceUnitTest {
    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private Environment env;

    private MockHttpServletRequest request = new MockHttpServletRequest();

    @Test
    @DisplayName("상품 등록 성공")
    void postProductSuccess1() {
        //given
        request.addHeader(AUTHORIZATION, "Bearer token");
        String mainTitle = "메인 제목";

        ProductDto productDto = new ProductDto(1L, mainTitle, "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000, 9000, "취급 방법", "원산지", "공급자", "https://메인이미지", "https://image1", "https://image2", "https://image3");
        Category category = new Category();

        given(tokenProvider.resolveToken(any())).willReturn("token");
        LocalDateTime now = LocalDateTime.now();
        Member member = new Member(1L, "email@mail.com", "password", "nickname", "name", "19941030", Role.SELLER, null, null, now, now);
        given(memberRepository.findById(any())).willReturn(Optional.of(member));
        given(productRepository.save(any())).willReturn(Product.of(member, category, productDto));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(new Category()));
        //when
        ProductVo productVo = productService.postProduct(productDto, request);

        //then
        assertThat(productVo.getMainTitle()).isEqualTo(mainTitle);
    }

    @Test
    @DisplayName("상품 카테고리가 존재하지 않아 실패")
    void postProductFail1() {
        //given
        request.addHeader(AUTHORIZATION, "Bearer token");
        String mainTitle = "메인 제목";

        ProductDto productDto = new ProductDto(0L, mainTitle, "메인 설명", "상품 메인 설명", "상품 서브 설명", 10000, 9000, "취급 방법", "원산지", "공급자", "https://메인이미지", "https://image1", "https://image2", "https://image3");
        //when
        Throwable throwable = catchThrowable(() -> productService.postProduct(productDto, request));

        //then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 카테고리");

    }

    @Test
    @DisplayName("상품 조회에 실패")
    void getProductFail1() {
        //given
        //when
        Throwable throwable = catchThrowable(() -> productService.getProduct(0L));

        //then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 상품");
    }

    @Test
    @DisplayName("수정하려는 상품의 카테고리가 존재하지 않는 경우 실패")
    void putProductFail1(){
        // given
        PutProductDto dto = new PutProductDto(0L, 0L, null, null,null,null,0,0,null,null,null,null,null,null,null);
        // when
        Throwable throwable = catchThrowable(() -> productService.putProduct(dto));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 카테고리");
    }


    @Test
    @DisplayName("수정하려는 상품이 존재하지 않는 경우 실패")
    void putProductFail2(){
        // given
        PutProductDto dto = new PutProductDto(0L, 1L, null, null,null,null,0,0,null,null,null,null,null,null,null);
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(new Category()));
        given(productRepository.findById(any())).willReturn(Optional.ofNullable(null));

        // when
        Throwable throwable = catchThrowable(() -> productService.putProduct(dto));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은 상품");
    }
}