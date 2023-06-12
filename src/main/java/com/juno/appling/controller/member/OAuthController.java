package com.juno.appling.controller.member;

import com.juno.appling.domain.dto.Api;
import com.juno.appling.domain.vo.member.LoginVo;
import com.juno.appling.service.member.MemberAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.juno.appling.domain.enums.ResultCode.SUCCESS;

@RestController
@RequestMapping("${api-prefix}/oauth")
@RequiredArgsConstructor
public class OAuthController {
    private final MemberAuthService memberAuthService;

    @GetMapping("/kakao")
    public ResponseEntity<Api<LoginVo>> login(@RequestParam String code){
        return ResponseEntity.ok(
                Api.<LoginVo>builder()
                        .code(SUCCESS.code)
                        .message(SUCCESS.message)
                        .data(memberAuthService.loginKakao(code))
                        .build()
        );
    }
}
