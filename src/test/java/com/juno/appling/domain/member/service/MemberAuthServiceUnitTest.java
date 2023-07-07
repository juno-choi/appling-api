package com.juno.appling.domain.member.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juno.appling.config.security.TokenProvider;
import com.juno.appling.domain.member.dto.JoinDto;
import com.juno.appling.domain.member.dto.kakao.KakaoLoginResponseDto;
import com.juno.appling.domain.member.entity.Member;
import com.juno.appling.domain.member.enums.SnsJoinType;
import com.juno.appling.domain.member.service.MemberAuthService;
import com.juno.appling.domain.member.vo.LoginVo;
import com.juno.appling.domain.member.repository.MemberRepository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.*;
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
    private final String TYPE = "Bearer";

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
        memberAuthService = new MemberAuthService(
                memberRepository, passwordEncoder, authenticationManagerBuilder, tokenProvider, redisTemplate, webClient, webClient, env
        );
    }

    @Test
    @DisplayName("kakao auth success")
    void kakaoAuthSuccess() throws Exception {
        //given
        given(env.getProperty(eq("kakao.client-id"))).willReturn("kakao client id");
        KakaoLoginResponseDto dto = KakaoLoginResponseDto.builder()
                .access_token("access token")
                .expires_in(1L)
                .refresh_token("refresh token")
                .refresh_token_expires_in(2L)
                .build();
        mockWebServer.enqueue(new MockResponse().setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).setBody(objectMapper.writeValueAsString(dto)));
        //when
        LoginVo kakaoLoginToken = memberAuthService.authKakao("kakao login code");
        //then
        Assertions.assertThat(kakaoLoginToken).isNotNull();
    }


    @Test
    @DisplayName("kakao 이메일이 존재하지 않는 회원은 fail")
    void kakaoLoginFail1() throws Exception {
        //given
        String kakaoReturnString = "{\n" +
                "    \"id\": 2727704352,\n" +
                "    \"connected_at\": \"2023-03-29T23:31:57Z\",\n" +
                "    \"properties\": {\n" +
                "        \"nickname\": \"최준호\",\n" +
                "        \"profile_image\": \"http://k.kakaocdn.net/dn/cpEwh1/btsfeggLuQz/AxpdbDgopjdSz6a36cjksK/img_640x640.jpg\",\n" +
                "        \"thumbnail_image\": \"http://k.kakaocdn.net/dn/cpEwh1/btsfeggLuQz/AxpdbDgopjdSz6a36cjksK/img_110x110.jpg\"\n" +
                "    },\n" +
                "    \"kakao_account\": {\n" +
                "        \"profile_nickname_needs_agreement\": false,\n" +
                "        \"profile_image_needs_agreement\": false,\n" +
                "        \"profile\": {\n" +
                "            \"nickname\": \"최준호\",\n" +
                "            \"thumbnail_image_url\": \"http://k.kakaocdn.net/dn/cpEwh1/btsfeggLuQz/AxpdbDgopjdSz6a36cjksK/img_110x110.jpg\",\n" +
                "            \"profile_image_url\": \"http://k.kakaocdn.net/dn/cpEwh1/btsfeggLuQz/AxpdbDgopjdSz6a36cjksK/img_640x640.jpg\",\n" +
                "            \"is_default_image\": false\n" +
                "        },\n" +
                "        \"has_email\": false,\n" +
                "        \"email_needs_agreement\": false,\n" +
                "        \"is_email_valid\": true,\n" +
                "        \"is_email_verified\": true,\n" +
                "        \"email\": \"ililil9482@naver.com\",\n" +
                "        \"has_age_range\": true,\n" +
                "        \"age_range_needs_agreement\": false,\n" +
                "        \"age_range\": \"30~39\",\n" +
                "        \"has_birthday\": true,\n" +
                "        \"birthday_needs_agreement\": false,\n" +
                "        \"birthday\": \"1030\",\n" +
                "        \"birthday_type\": \"SOLAR\",\n" +
                "        \"has_gender\": true,\n" +
                "        \"gender_needs_agreement\": false,\n" +
                "        \"gender\": \"male\"\n" +
                "    }\n" +
                "}";
        mockWebServer.enqueue(new MockResponse().setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).setBody(kakaoReturnString));
        //when
        Throwable throwable = catchThrowable(() -> memberAuthService.loginKakao("kakao_access_token"));

        //then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이메일이 존재하지 않는 회원입니다.");
    }

    @Test
    @DisplayName("kakao 전달 받은 내용이 없어 fail")
    void kakaoLoginFail2() throws Exception {
        //given
        String kakaoReturnString = "";
        mockWebServer.enqueue(new MockResponse().setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).setBody(kakaoReturnString));
        //when
        Throwable throwable = catchThrowable(() -> memberAuthService.loginKakao("kakao_access_token"));

        //then
        assertThat(throwable).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("카카오에서 반환 받은 값이 존재하지 않습니다.");
    }


    @Test
    @DisplayName("kakao login success")
    void kakaoLoginSuccess() throws Exception {
        //given
        String kakaoReturnString = "{\n" +
                "    \"id\": 2727704352,\n" +
                "    \"connected_at\": \"2023-03-29T23:31:57Z\",\n" +
                "    \"properties\": {\n" +
                "        \"nickname\": \"최준호\",\n" +
                "        \"profile_image\": \"http://k.kakaocdn.net/dn/cpEwh1/btsfeggLuQz/AxpdbDgopjdSz6a36cjksK/img_640x640.jpg\",\n" +
                "        \"thumbnail_image\": \"http://k.kakaocdn.net/dn/cpEwh1/btsfeggLuQz/AxpdbDgopjdSz6a36cjksK/img_110x110.jpg\"\n" +
                "    },\n" +
                "    \"kakao_account\": {\n" +
                "        \"profile_nickname_needs_agreement\": false,\n" +
                "        \"profile_image_needs_agreement\": false,\n" +
                "        \"profile\": {\n" +
                "            \"nickname\": \"최준호\",\n" +
                "            \"thumbnail_image_url\": \"http://k.kakaocdn.net/dn/cpEwh1/btsfeggLuQz/AxpdbDgopjdSz6a36cjksK/img_110x110.jpg\",\n" +
                "            \"profile_image_url\": \"http://k.kakaocdn.net/dn/cpEwh1/btsfeggLuQz/AxpdbDgopjdSz6a36cjksK/img_640x640.jpg\",\n" +
                "            \"is_default_image\": false\n" +
                "        },\n" +
                "        \"has_email\": true,\n" +
                "        \"email_needs_agreement\": false,\n" +
                "        \"is_email_valid\": true,\n" +
                "        \"is_email_verified\": true,\n" +
                "        \"email\": \"ililil9482@naver.com\",\n" +
                "        \"has_age_range\": true,\n" +
                "        \"age_range_needs_agreement\": false,\n" +
                "        \"age_range\": \"30~39\",\n" +
                "        \"has_birthday\": true,\n" +
                "        \"birthday_needs_agreement\": false,\n" +
                "        \"birthday\": \"1030\",\n" +
                "        \"birthday_type\": \"SOLAR\",\n" +
                "        \"has_gender\": true,\n" +
                "        \"gender_needs_agreement\": false,\n" +
                "        \"gender\": \"male\"\n" +
                "    }\n" +
                "}";
        mockWebServer.enqueue(new MockResponse().setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).setBody(kakaoReturnString));

        given(memberRepository.findByEmail(anyString())).willReturn(Optional.ofNullable(null));
        given(tokenProvider.generateTokenDto(any())).willReturn(
                LoginVo.builder()
                        .accessToken("access token")
                        .accessTokenExpired(1L)
                        .refreshToken("refresh token")
                        .type(TYPE)
                        .build());
        String snsId = "snsId";
        JoinDto joinDto = new JoinDto("kakao@email.com", snsId, "카카오회원", "카카오회원", null);
        given(memberRepository.save(any())).willReturn(Member.of(joinDto, snsId, SnsJoinType.KAKAO));
        given(authenticationManagerBuilder.getObject()).willReturn(mockAuthenticationManager());

        //when
        LoginVo loginVo = memberAuthService.loginKakao("kakao_access_token");

        //then
        assertThat(loginVo).isNotNull();
    }

    private AuthenticationManager mockAuthenticationManager() {
        return authentication -> new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return null;
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return null;
            }
        };
    }
}