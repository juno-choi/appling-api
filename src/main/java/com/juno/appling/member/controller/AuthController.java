package com.juno.appling.member.controller;

import com.juno.appling.config.base.Api;
import com.juno.appling.member.domain.dto.JoinDto;
import com.juno.appling.member.domain.dto.LoginDto;
import com.juno.appling.member.domain.vo.JoinVo;
import com.juno.appling.member.domain.vo.LoginVo;
import com.juno.appling.member.service.MemberAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.juno.appling.config.base.ResultCode.POST;
import static com.juno.appling.config.base.ResultCode.SUCCESS;

@RestController
@RequestMapping("${api-prefix}/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberAuthService memberAuthService;

    @PostMapping("/join")
    public ResponseEntity<Api<JoinVo>> join(@RequestBody @Validated JoinDto joinDto,
        BindingResult bindingResult) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Api.<JoinVo>builder()
            .code(POST.code)
            .message(POST.message)
            .data(memberAuthService.join(joinDto))
            .build());
    }

    @PostMapping("/login")
    public ResponseEntity<Api<LoginVo>> login(@RequestBody @Validated LoginDto loginDto,
        BindingResult bindingResult) {
        return ResponseEntity.ok(
            Api.<LoginVo>builder()
                .code(SUCCESS.code)
                .message(SUCCESS.message)
                .data(memberAuthService.login(loginDto))
                .build()
        );
    }

    @GetMapping("/refresh/{refresh_token}")
    public ResponseEntity<Api<LoginVo>> refresh(
        @PathVariable(value = "refresh_token") String refreshToken) {
        return ResponseEntity.ok(
            Api.<LoginVo>builder()
                .code(SUCCESS.code)
                .message(SUCCESS.message)
                .data(memberAuthService.refresh(refreshToken))
                .build()
        );
    }
}
