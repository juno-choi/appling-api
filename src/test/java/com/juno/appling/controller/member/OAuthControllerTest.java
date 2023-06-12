package com.juno.appling.controller.member;

import com.juno.appling.BaseTest;
import com.juno.appling.domain.dto.member.KakaoLoginResponseDto;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.reactive.function.client.WebClient;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class OAuthControllerTest extends BaseTest {
    private static MockWebServer mockWebServer;

    private final String PREFIX = "/api/oauth";

    @MockBean
    private WebClient kakaoClient;

    private static WebClient webClient;

    @BeforeAll
    static void setUp() throws Exception{
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        webClient = WebClient.create("http://localhost:"+mockWebServer.getPort());
    }

    @AfterAll
    static void tearDown() throws Exception {
        mockWebServer.shutdown();
    }


    @Test
    @DisplayName(PREFIX+"/kakao")
    void oauthKakao() throws Exception {
        //given
        given(kakaoClient.post()).willReturn(webClient.post());

        KakaoLoginResponseDto dto = KakaoLoginResponseDto.builder()
                .access_token("access token")
                .expires_in(1L)
                .refresh_token("refresh token")
                .refresh_token_expires_in(2L)
                .build();
        mockWebServer.enqueue(new MockResponse().setHeader("Content-Type", MediaType.APPLICATION_JSON).setBody(objectMapper.writeValueAsString(dto)));
        //when
        ResultActions resultActions = mock.perform(
                get(PREFIX + "/kakao").param("code", "kakao login token")
        ).andDo(print());
        //then

    }

}