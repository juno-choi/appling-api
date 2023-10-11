package com.juno.appling;

import com.juno.appling.member.application.MemberAuthService;
import com.juno.appling.member.dto.request.JoinRequest;
import com.juno.appling.member.domain.Introduce;
import com.juno.appling.member.domain.Member;
import com.juno.appling.member.domain.Seller;
import com.juno.appling.member.dto.request.LoginRequest;
import com.juno.appling.member.dto.response.LoginResponse;
import com.juno.appling.member.enums.IntroduceStatus;
import com.juno.appling.member.enums.Role;
import com.juno.appling.member.domain.IntroduceRepository;
import com.juno.appling.member.domain.MemberRepository;
import com.juno.appling.member.domain.SellerRepository;
import com.juno.appling.product.domain.Category;
import com.juno.appling.product.domain.Product;
import com.juno.appling.product.domain.ProductRepository;
import com.juno.appling.product.dto.request.ProductRequest;
import com.juno.appling.product.enums.CategoryStatus;
import com.juno.appling.product.domain.CategoryRepository;
import com.juno.appling.product.enums.ProductStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Optional;

@Execution(ExecutionMode.SAME_THREAD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseTest {

    protected ObjectMapper objectMapper = new ObjectMapper();
    protected String MEMBER_EMAIL = "member@appling.com";
    protected String SELLER_EMAIL = "seller@appling.com";
    protected String SELLER2_EMAIL = "seller2@appling.com";
    protected String PASSWORD = "password";

    protected Member MEMBER = null;
    protected Seller SELLER1 = null;
    protected Seller SELLER2 = null;
    protected Product PRODUCT1 = null;
    protected Product PRODUCT2 = null;

    protected String MEAL_CATEGORY = "육류";
    protected String FRUIT_CATEGORY = "과일";
    protected String VEGETABLE_CATEGORY = "야채";

    protected LoginResponse SELLER_LOGIN;

    protected LoginResponse MEMBER_LOGIN;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MemberAuthService memberAuthService;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private IntroduceRepository introduceRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    @BeforeAll
    @Order(1)
    void setUp() {
        /**
         * 초기 회원 세팅
         */
        String[] emailList = {MEMBER_EMAIL, SELLER_EMAIL, SELLER2_EMAIL};

        for (String email : emailList) {
            Optional<Member> findMember = memberRepository.findByEmail(email);
            if (findMember.isEmpty()) {
                JoinRequest joinRequest = new JoinRequest(email, passwordEncoder.encode(PASSWORD), "name",
                    "nick", "19941030");
                Member saveMember = memberRepository.save(Member.of(joinRequest));

                if(saveMember.getEmail().equals(MEMBER_EMAIL)) {
                    MEMBER = saveMember;
                }
                if (saveMember.getEmail().equals(SELLER_EMAIL) || saveMember.getEmail().equals(SELLER2_EMAIL)) {
                    /**
                     * 판매자 세팅
                     */
                    if (saveMember.getEmail().equals(SELLER_EMAIL)) {
                        saveMember.patchMemberRole(Role.SELLER);
                        SELLER1 = sellerRepository.save(
                            Seller.of(saveMember, "애플링", "01012344321", "1234", "강원도 평창군 장미산길 126", "상세주소",
                                "email@mail.com"));
                    }
                    if (saveMember.getEmail().equals(SELLER2_EMAIL)) {
                        saveMember.patchMemberRole(Role.SELLER);
                        SELLER2 = sellerRepository.save(
                            Seller.of(saveMember, "자연농원", "01012344321", "1234", "강원도 평창군 장미산길 126", "상세주소",
                                "email@mail.com"));
                        introduceRepository.save(Introduce.of(SELLER2, "장미산길",
                            "https://appling-s3-bucket.s3.ap-northeast-2.amazonaws.com/html/1/20230815/184934_0.html",
                            IntroduceStatus.USE));
                    }
                }
            }
        }

        /**
         * 카테고리 정보 세팅
         */
        String[] categoryList = {FRUIT_CATEGORY, MEAL_CATEGORY, VEGETABLE_CATEGORY};

        for (String category : categoryList) {
            Optional<Category> findCategory = categoryRepository.findByName(category);
            if (findCategory.isEmpty()) {
                categoryRepository.save(Category.of(category, CategoryStatus.USE));
            }
        }

        /**
         * 상품 등록
         */
        Category category = categoryRepository.findByName(FRUIT_CATEGORY).get();
        ProductRequest productRequest1 = new ProductRequest(category.getId(), "테스트 상품1", "테스트", "테스트", "테스트 설명", 10000, 9000, "주의 사항", "원산지", "공급자", "이미지", "이미지1", "", "", ProductStatus.NORMAL.name(), 10, new ArrayList<>(), "normal");
        ProductRequest productRequest2 = new ProductRequest(category.getId(), "테스트 상품2", "테스트", "테스트", "테스트 설명", 10000, 9000, "주의 사항", "원산지", "공급자", "이미지", "이미지1", "", "", ProductStatus.NORMAL.name(), 10, new ArrayList<>(), "normal");
        PRODUCT1 = productRepository.save(Product.of(SELLER1, category, productRequest1));
        PRODUCT2 = productRepository.save(Product.of(SELLER2, category, productRequest2));

        /**
         * 회원 로그인
         */
        LoginRequest sellerLoginRequest = new LoginRequest(SELLER_EMAIL, PASSWORD);
        SELLER_LOGIN = memberAuthService.login(sellerLoginRequest);
    }

}
