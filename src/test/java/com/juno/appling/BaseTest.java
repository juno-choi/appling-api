package com.juno.appling;

import com.juno.appling.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

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

    @BeforeAll
    void setUp(){
        System.out.println("실행 전 테스트");
    }

}
