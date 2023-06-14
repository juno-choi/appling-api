package com.juno.appling.service.member;

import com.juno.appling.domain.vo.member.LoginVo;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class MemberAuthServiceTest {

    @Autowired
    private MemberAuthService memberAuthService;


    @Test
    @Disabled("실제 카카오 로그인 코드 값을 받아와야 가능")
    @DisplayName("kakao auth success")
    void kakaoAuthSuccess() throws Exception {
        //given
        //when
        LoginVo kakaoLoginToken = memberAuthService.authKakao("kakao login code");
        //then
        Assertions.assertThat(kakaoLoginToken).isNotNull();
    }

    @Test
    @Disabled("실제 카카오 로그인 access token 값을 받아와야 가능")
    @DisplayName("kakao login success")
    void kakaoLoginSuccess() throws Exception {
        //given
        //when
        memberAuthService.loginKakao("c5gOv5YdZbUEKY0EUGzC-nd-gIKN7kVvQ24qhGQiCinI2gAAAYi4Gfl3");
        //then
    }
}