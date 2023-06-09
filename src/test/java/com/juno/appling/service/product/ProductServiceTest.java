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
import org.springframework.security.core.parameters.P;
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
}