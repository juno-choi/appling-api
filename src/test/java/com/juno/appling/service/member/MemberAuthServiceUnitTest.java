package com.juno.appling.service.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juno.appling.common.security.TokenProvider;
import com.juno.appling.domain.dto.member.KakaoLoginResponseDto;
import com.juno.appling.repository.member.MemberRepository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith({MockitoExtension.class})
class MemberAuthServiceUnitTest {
    private MemberAuthService memberAuthService;

    @Mock
    private Environment env;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private RedisTemplate redisTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();
    private static MockWebServer mockWebServer;

    @BeforeAll
    static void setUp() throws Exception{
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:%s",mockWebServer.getPort());
        final WebClient webClient = WebClient.create(baseUrl);
        memberAuthService = new MemberAuthService(memberRepository, passwordEncoder, authenticationManagerBuilder, tokenProvider, redisTemplate, webClient, env);
    }

    @Test
    @DisplayName("test")
    void test() throws Exception {
        //given
        given(env.getProperty(eq("kakao.client_id"))).willReturn("kakao client id");
        KakaoLoginResponseDto dto = KakaoLoginResponseDto.builder()
                .access_token("access token")
                .expires_in(1L)
                .refresh_token("refresh token")
                .refresh_token_expires_in(2L)
                .build();
        mockWebServer.enqueue(new MockResponse().setHeader("Content-Type", MediaType.APPLICATION_JSON).setBody(objectMapper.writeValueAsString(dto)));
        mockWebServer.enqueue(new MockResponse().setHeader("Content-Type", MediaType.APPLICATION_JSON).setBody(objectMapper.writeValueAsString(dto)));
        //when
        memberAuthService.loginKakao("kakao login token");
        //then

    }

}