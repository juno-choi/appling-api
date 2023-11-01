package com.juno.appling.product.service;

import static com.juno.appling.Base.CATEGORY_ID_FRUIT;
import static com.juno.appling.Base.PRODUCT_ID_APPLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.juno.appling.global.security.TokenProvider;
import com.juno.appling.member.domain.Member;
import com.juno.appling.member.domain.Seller;
import com.juno.appling.member.enums.Role;
import com.juno.appling.member.infrastruceture.MemberRepository;
import com.juno.appling.member.infrastruceture.SellerRepository;
import com.juno.appling.product.controller.request.AddViewCntRequest;
import com.juno.appling.product.controller.request.OptionRequest;
import com.juno.appling.product.controller.request.ProductRequest;
import com.juno.appling.product.controller.request.PutProductRequest;
import com.juno.appling.product.controller.response.ProductResponse;
import com.juno.appling.product.domain.Category;
import com.juno.appling.product.domain.Option;
import com.juno.appling.product.domain.Product;
import com.juno.appling.product.enums.OptionStatus;
import com.juno.appling.product.enums.ProductStatus;
import com.juno.appling.product.enums.ProductType;
import com.juno.appling.product.infrastructure.CategoryRepository;
import com.juno.appling.product.infrastructure.OptionRepository;
import com.juno.appling.product.infrastructure.ProductCustomRepositoryImpl;
import com.juno.appling.product.infrastructure.ProductRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletRequest;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplUnitTest {

    @InjectMocks
    private ProductServiceImpl productService;
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
    private ProductCustomRepositoryImpl productCustomRepositoryImpl;

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
        OptionRequest optionRequest1 = OptionRequest.builder()
            .name("option1")
            .extraPrice(1000)
            .status(OptionStatus.NORMAL.name())
            .ea(100)
            .build();
        optionRequestList.add(optionRequest1);

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
            .optionList(optionRequestList)
            .type("option")
            .build();
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
        OptionRequest optionRequest1 = OptionRequest.builder()
            .optionId(1L)
            .name("option2")
            .extraPrice(1000)
            .status(OptionStatus.NORMAL.name())
            .ea(100)
            .build();
        OptionRequest optionRequest2 = OptionRequest.builder()
            .name("option2")
            .extraPrice(1000)
            .status(OptionStatus.NORMAL.name())
            .ea(100)
            .build();
        optionRequestList.add(optionRequest1);
        optionRequestList.add(optionRequest2);
        LocalDateTime now = LocalDateTime.now();

        Member member = new Member(1L, "email@mail.com", "password", "nickname", "name", "19941030",
            Role.SELLER, null, null, now, now);
        Seller seller = Seller.of(member, "회사명", "01012344321", "1234", "회사 주소", "상세 주소", "mail@mail.com");
        Category category = new Category();
        Product product = Product.builder()
            .id(PRODUCT_ID_APPLE)
            .seller(seller)
            .category(category)
            .mainTitle("메인 제목")
            .mainExplanation("메인 설명")
            .productMainExplanation("상품 메인 설명")
            .productSubExplanation("상품 서브 설명")
            .price(10000)
            .originPrice(9000)
            .mainImage("https://메인이미지")
            .image1("https://image1")
            .image2("https://image2")
            .image3("https://image3")
            .status(ProductStatus.NORMAL)
            .createAt(now)
            .modifiedAt(now)
            .type(ProductType.OPTION)
            .build();
        product.addAllOptionsList(new ArrayList<>());

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
            () -> productService.addViewCnt(AddViewCntRequest.builder().productId(1L).build()));
        // then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("유효하지 않은 상품");
    }

}