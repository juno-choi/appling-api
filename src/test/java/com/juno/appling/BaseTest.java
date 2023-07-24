package com.juno.appling;

import com.juno.appling.domain.member.dto.JoinDto;
import com.juno.appling.domain.member.entity.Member;
import com.juno.appling.domain.member.repository.MemberRepository;
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

    @Autowired
    private MemberRepository memberRepository;

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
                memberRepository.save(Member.of(joinDto));
                System.out.println(email+" 가입 완료");
            }
        }
    }

}
