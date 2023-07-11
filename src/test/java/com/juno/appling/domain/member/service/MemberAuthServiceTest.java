package com.juno.appling.domain.member.service;

import com.juno.appling.domain.member.record.LoginRecord;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberAuthServiceTest {

    @Autowired
    private MemberAuthService memberAuthService;


    @Test
    @Disabled("실제 카카오 로그인 코드 값을 받아와야 가능")
    @DisplayName("kakao auth success")
    void kakaoAuthSuccess() throws Exception {
        //given
        //when
        LoginRecord kakaoLoginToken = memberAuthService.authKakao("kakao login code");
        //then
        assertThat(kakaoLoginToken).isNotNull();
    }

    @Test
    @Disabled("실제 카카오 로그인 access token 값을 받아와야 가능")
    @DisplayName("kakao login success")
    void kakaoLoginSuccess() throws Exception {
        //given
        //when
        LoginRecord loginRecord = memberAuthService.loginKakao("c5gOv5YdZbUEKY0EUGzC-nd-gIKN7kVvQ24qhGQiCinI2gAAAYi4Gfl3");
        //then
        assertThat(loginRecord).isNotNull();
    }
}