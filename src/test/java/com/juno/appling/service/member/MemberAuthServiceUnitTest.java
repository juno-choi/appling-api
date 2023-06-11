package com.juno.appling.service.member;

import com.juno.appling.domain.dto.member.KakaoLoginResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class MemberAuthServiceUnitTest {
    @InjectMocks
    private MemberAuthService memberAuthService;

    @Mock
    private Environment env;

    @Mock
    private WebClient webClient;

    private WebClient.RequestBodyUriSpec requestBodyUriMock;
    private WebClient.RequestHeadersSpec requestHeadersMock;
    private WebClient.RequestBodySpec requestBodyMock;
    private WebClient.ResponseSpec responseMock;
    private WebClient webClientMock;

    @BeforeEach
    void mockWebClient() {
        requestBodyUriMock = mock(WebClient.RequestBodyUriSpec.class);
        requestHeadersMock = mock(WebClient.RequestHeadersSpec.class);
        requestBodyMock = mock(WebClient.RequestBodySpec.class);
        responseMock = mock(WebClient.ResponseSpec.class);
        webClientMock = mock(WebClient.class);
    }

    @Test
    @DisplayName("test")
    void test() {
        //given
        String expectedUri = "https://kauth.kakao.com/oauth/token";
        given(env.getProperty(eq("kakao.client_id"))).willReturn("kakao client id");

        when(webClientMock.post()).thenReturn(requestBodyUriMock);
        when(requestBodyUriMock.uri(eq(expectedUri))).thenReturn(requestBodyMock);
        when(requestBodyMock.bodyValue(any())).thenReturn(requestHeadersMock);
        when(requestHeadersMock.retrieve()).thenReturn(responseMock);
        when(responseMock.bodyToMono(KakaoLoginResponseDto.class)).thenReturn(Mono.just(KakaoLoginResponseDto.builder().build()));

        //when
        memberAuthService.loginKakao("kakao login token");
        //then

    }

}