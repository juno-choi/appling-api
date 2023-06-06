package com.juno.appling.service.product;

import com.juno.appling.domain.dto.member.LoginDto;
import com.juno.appling.domain.dto.product.ProductDto;
import com.juno.appling.domain.entity.product.Product;
import com.juno.appling.domain.vo.member.LoginVo;
import com.juno.appling.domain.vo.product.ProductVo;
import com.juno.appling.repository.product.ProductRepository;
import com.juno.appling.service.member.MemberAuthService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest
@Transactional
class ProductServiceTest {
    @Autowired
    private ProductService productService;
    @Autowired
    private MemberAuthService memberAuthService;

    @Autowired
    private ProductRepository productRepository;

    private MockHttpServletRequest request = new MockHttpServletRequest();

    @Test
    @Disabled
    @DisplayName("상품 등록 성공")
    void postProduct() {
        //given
        LoginDto loginDto = new LoginDto("seller@appling.com", "password");
        LoginVo login = memberAuthService.login(loginDto);
        request.addHeader(AUTHORIZATION, "Bearer "+login.getAccessToken());

        ProductDto dto = ProductDto.builder()
                .mainTitle("메인 제목")
                .mainExplanation("메인 설명")
                .productMainExplanation("상품 메인 설명")
                .productSubExplanation("상품 서브 설명")
                .producer("공급자")
                .origin("원산지")
                .purchaseInquiry("취급 방법")
                .originPrice(1000)
                .price(100)
                .mainImage("https://image0")
                .image1("https://image1")
                .image2("https://image2")
                .image3("https://image3")
                .build();
        //when
        ProductVo productVo = productService.postProduct(dto, request);

        //then
        Optional<Product> byId = productRepository.findById(productVo.getId());
        Product product = byId.get();
        String email = product.getMember().getEmail();

        assertThat(byId).isNotEmpty();
        assertThat(email).isEqualTo(loginDto.getEmail());
    }
}