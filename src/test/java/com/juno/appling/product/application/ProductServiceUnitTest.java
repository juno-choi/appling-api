package com.juno.appling.product.application;

import com.juno.appling.global.security.TokenProvider;
import com.juno.appling.member.domain.Member;
import com.juno.appling.member.domain.MemberRepository;
import com.juno.appling.member.domain.Seller;
import com.juno.appling.member.domain.SellerRepository;
import com.juno.appling.member.enums.Role;
import com.juno.appling.product.domain.*;
import com.juno.appling.product.dto.request.AddViewCntRequest;
import com.juno.appling.product.dto.request.OptionRequest;
import com.juno.appling.product.dto.request.ProductRequest;
import com.juno.appling.product.dto.request.PutProductRequest;
import com.juno.appling.product.dto.response.ProductResponse;
import com.juno.appling.product.enums.OptionStatus;
import com.juno.appling.product.enums.ProductStatus;
import com.juno.appling.product.enums.ProductType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
    private SellerRepository sellerRepository;

    @Mock
    private ProductCustomRepository productCustomRepository;

    @Mock
    private OptionRepository optionRepository;

    @Mock
    private Environment env;

    private MockHttpServletRequest request = new MockHttpServletRequest();

    @BeforeEach
    void setUp() {
        request.addHeader(AUTHORIZATION, "Bearer token");
    }

    @Test
    @DisplayName("상품 등록 성공")
    void postProductSuccess1() {
        //given
        String mainTitle = "메인 제목";

        ProductRequest productRequest = new ProductRequest(1L, mainTitle, "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000, 9000, "취급 방법", "원산지", "공급자", "https://메인이미지", "https://image1", "https://image2",
            "https://image3", "normal", 10, null, "normal");
        Category category = new Category();

        given(tokenProvider.resolveToken(any())).willReturn("token");
        LocalDateTime now = LocalDateTime.now();
        Member member = new Member(1L, "email@mail.com", "password", "nickname", "name", "19941030",
            Role.SELLER, null, null, now, now);
        Seller seller = Seller.of(member, "회사명", "01012344321", "1234", "회사 주소", "상세 주소", "mail@mail.com");
        given(memberRepository.findById(any())).willReturn(Optional.of(member));
        given(sellerRepository.findByMember(any())).willReturn(Optional.of(seller));
        given(productRepository.save(any())).willReturn(Product.of(seller, category, productRequest));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(new Category()));
        //when
        ProductResponse productResponse = productService.postProduct(productRequest, request);

        //then
        assertThat(productResponse.getMainTitle()).isEqualTo(mainTitle);
    }

    @Test
    @DisplayName("옵션 상품 등록 성공")
    void postProductSuccess2() {
        //given
        String mainTitle = "메인 제목";

        List<OptionRequest> optionRequestList = new ArrayList<>();
        OptionRequest optionRequest1 = new OptionRequest(null, "option1", 1000, OptionStatus.NORMAL.name(), 100);
        optionRequestList.add(optionRequest1);

        ProductRequest productRequest = new ProductRequest(1L, mainTitle, "메인 설명", "상품 메인 설명", "상품 서브 설명",
                10000, 9000, "취급 방법", "원산지", "공급자", "https://메인이미지", "https://image1", "https://image2",
                "https://image3", "normal", 10, optionRequestList, "option");
        Category category = new Category();

        given(tokenProvider.resolveToken(any())).willReturn("token");
        LocalDateTime now = LocalDateTime.now();
        Member member = new Member(1L, "email@mail.com", "password", "nickname", "name", "19941030",
                Role.SELLER, null, null, now, now);
        Seller seller = Seller.of(member, "회사명", "01012344321", "1234", "회사 주소", "상세 주소", "mail@mail.com");
        given(memberRepository.findById(any())).willReturn(Optional.of(member));
        given(sellerRepository.findByMember(any())).willReturn(Optional.of(seller));
        given(productRepository.save(any())).willReturn(Product.of(seller, category, productRequest));
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(new Category()));
        //when
        ProductResponse productResponse = productService.postProduct(productRequest, request);

        //then
        assertThat(productResponse.getMainTitle()).isEqualTo(mainTitle);
    }

    @Test
    @DisplayName("상품 카테고리가 존재하지 않아 실패")
    void postProductFail1() {
        //given
        String mainTitle = "메인 제목";

        ProductRequest productRequest = new ProductRequest(0L, mainTitle, "메인 설명", "상품 메인 설명", "상품 서브 설명",
            10000, 9000, "취급 방법", "원산지", "공급자", "https://메인이미지", "https://image1", "https://image2",
            "https://image3", "normal", 10, null, "normal");
        //when
        Throwable throwable = catchThrowable(() -> productService.postProduct(productRequest, request));

        //then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("유효하지 않은 카테고리");

    }

    @Test
    @DisplayName("상품 타입이 존재하지 않아 실패")
    void postProductFail2() {
        //given
        String mainTitle = "메인 제목";

        ProductRequest productRequest = new ProductRequest(0L, mainTitle, "메인 설명", "상품 메인 설명", "상품 서브 설명",
                10000, 9000, "취급 방법", "원산지", "공급자", "https://메인이미지", "https://image1", "https://image2",
                "https://image3", "normal", 10, null, "normal");
        //when
        Throwable throwable = catchThrowable(() -> productService.postProduct(productRequest, request));

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
    void putProductFail1() {
        // given
        PutProductRequest dto = PutProductRequest.builder()
                .status("normal")
                .build();
        // when
        Throwable throwable = catchThrowable(() -> productService.putProduct(dto));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("유효하지 않은 카테고리");
    }


    @Test
    @DisplayName("수정하려는 상품이 존재하지 않는 경우 실패")
    void putProductFail2() {
        // given
        PutProductRequest dto = PutProductRequest.builder()
                .categoryId(1L)
                .status("normal")
                .build();
        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(new Category()));
        given(productRepository.findById(any())).willReturn(Optional.ofNullable(null));

        // when
        Throwable throwable = catchThrowable(() -> productService.putProduct(dto));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("유효하지 않은 상품");
    }

    @Test
    @DisplayName("옵션 상품 수정 성공")
    void putProductSuccess1() {
        // given
        List<OptionRequest> optionRequestList = new ArrayList<>();
        OptionRequest optionRequest1 = new OptionRequest(1L, "option2", 1000, OptionStatus.NORMAL.name(), 100);
        OptionRequest optionRequest2 = new OptionRequest(null, "option2", 1000, OptionStatus.NORMAL.name(), 100);
        optionRequestList.add(optionRequest1);
        optionRequestList.add(optionRequest2);
        LocalDateTime now = LocalDateTime.now();

        Member member = new Member(1L, "email@mail.com", "password", "nickname", "name", "19941030",
            Role.SELLER, null, null, now, now);
        Seller seller = Seller.of(member, "회사명", "01012344321", "1234", "회사 주소", "상세 주소", "mail@mail.com");
        Category category = new Category();
        Product product = new Product(1L, seller, category, "메인 제목", "메인 설명", "상품 메인 설명",
            "상품 서브 설명 ", 10000, 9000, "취급 방법", "원산지", "공급자", "https://메인이미지", "https://image1",
            "https://image2", "https://image3", 0L, ProductStatus.NORMAL, 100, now, now, new ArrayList<>(),
            ProductType.OPTION);
        Option option = new Option(1L, optionRequest1.getName(), optionRequest1.getExtraPrice(), optionRequest1.getEa(), OptionStatus.NORMAL, now, now, product);
        product.addOptionsList(option);

        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(category));
        given(productRepository.findById(any())).willReturn(Optional.ofNullable(product));

        PutProductRequest dto = PutProductRequest.builder()
                .productId(1L)
                .categoryId(1L)
                .status("normal")
                .ea(10)
                .optionList(optionRequestList)
                .build();

        // when
        ProductResponse productResponse = productService.putProduct(dto);
        // then
        assertThat(productResponse.getOptionList().get(0).getName()).isEqualTo(optionRequestList.get(0).getName());
    }

    @Test
    @DisplayName("존재하지 않은 상품은 조회수 증가에 실패")
    void addViewCnt() {
        // given
        // when
        Throwable throwable = catchThrowable(
            () -> productService.addViewCnt(new AddViewCntRequest(1L)));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("유효하지 않은 상품");
    }

}