package com.juno.appling;

import com.juno.appling.domain.member.dto.JoinDto;
import com.juno.appling.domain.member.entity.Member;
import com.juno.appling.domain.member.entity.Seller;
import com.juno.appling.domain.member.enums.Role;
import com.juno.appling.domain.member.repository.MemberRepository;
import com.juno.appling.domain.member.repository.SellerRepository;
import com.juno.appling.domain.product.entity.Category;
import com.juno.appling.domain.product.enums.CategoryStatus;
import com.juno.appling.domain.product.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

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

    protected String MEAL_CATEGORY = "육류";
    protected String FRUIT_CATEGORY = "과일";
    protected String VEGETABLE_CATEGORY = "야채";
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;


    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Transactional
    @BeforeAll
    void setUp(){
        /**
         * 초기 회원 세팅
         */
        String[] emailList = {MEMBER_EMAIL, SELLER_EMAIL, SELLER2_EMAIL};
        for(String email : emailList){
            Optional<Member> findMember = memberRepository.findByEmail(email);
            if(findMember.isEmpty()){
                JoinDto joinDto = new JoinDto(email, passwordEncoder.encode(PASSWORD), "name", "nick", "19941030");
                Member saveMember = memberRepository.save(Member.of(joinDto));
                if(saveMember.getEmail().equals(SELLER_EMAIL) || saveMember.getEmail().equals(SELLER2_EMAIL)){
                    /**
                     * 판매자 세팅
                     */
                    if(saveMember.getEmail().equals(SELLER_EMAIL)){
                        saveMember.patchMemberRole(Role.SELLER);
                        sellerRepository.save(Seller.of(saveMember, "애플링", "01012344321", "강원도 평창군 장미산길 126", "email@mail.com"));
                    }
                    if(saveMember.getEmail().equals(SELLER2_EMAIL)){
                        saveMember.patchMemberRole(Role.SELLER);
                        sellerRepository.save(Seller.of(saveMember, "자연농원", "01012344321", "강원도 평창군 장미산길 126", "email@mail.com"));
                    }
                }
            }
        }

        /**
         * 카테고리 정보 세팅
         */
        String[] categoryList = {FRUIT_CATEGORY, MEAL_CATEGORY, VEGETABLE_CATEGORY};

        for(String category : categoryList){
            Optional<Category> findCategory = categoryRepository.findByName(category);
            if(findCategory.isEmpty()){
                categoryRepository.save(Category.of(category ,CategoryStatus.USE));
            }
        }
    }

}
