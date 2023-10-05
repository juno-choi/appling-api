package com.juno.appling.member.presentation;

import com.juno.appling.global.base.Api;
import com.juno.appling.member.dto.response.LoginResponse;
import com.juno.appling.member.application.MemberAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.juno.appling.global.base.ResultCode.SUCCESS;

@RestController
@RequestMapping("${api-prefix}/oauth")
@RequiredArgsConstructor
public class OAuthController {

    private final MemberAuthService memberAuthService;

    @GetMapping("/kakao")
    public ResponseEntity<Api<LoginResponse>> authKakao(@RequestParam String code) {
        return ResponseEntity.ok(
            new Api<>(SUCCESS.code, SUCCESS.message, memberAuthService.authKakao(code))
        );
    }

    @GetMapping("/kakao/login")
    public ResponseEntity<Api<LoginResponse>> login(
        @RequestParam(name = "access_token") String accessToken) {
        return ResponseEntity.ok(
            new Api<>(SUCCESS.code, SUCCESS.message, memberAuthService.loginKakao(accessToken))
        );
    }
}
