package com.juno.appling.domain.member.controller;

import com.juno.appling.config.base.Api;
import com.juno.appling.domain.member.record.LoginRecord;
import com.juno.appling.domain.member.service.MemberAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.juno.appling.config.base.ResultCode.SUCCESS;

@RestController
@RequestMapping("${api-prefix}/oauth")
@RequiredArgsConstructor
public class OAuthController {
    private final MemberAuthService memberAuthService;

    @GetMapping("/kakao")
    public ResponseEntity<Api<LoginRecord>> authKakao(@RequestParam String code){
        return ResponseEntity.ok(
                Api.<LoginRecord>builder()
                        .code(SUCCESS.code)
                        .message(SUCCESS.message)
                        .data(memberAuthService.authKakao(code))
                        .build()
        );
    }

    @GetMapping("/kakao/login")
    public ResponseEntity<Api<LoginRecord>> login(@RequestParam(name = "access_token") String accessToken){
        return ResponseEntity.ok(
                Api.<LoginRecord>builder()
                        .code(SUCCESS.code)
                        .message(SUCCESS.message)
                        .data(memberAuthService.loginKakao(accessToken))
                        .build()
        );
    }
}
