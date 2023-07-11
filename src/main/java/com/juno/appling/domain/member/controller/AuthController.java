package com.juno.appling.domain.member.controller;

import com.juno.appling.config.base.Api;
import com.juno.appling.domain.member.dto.JoinDto;
import com.juno.appling.domain.member.dto.LoginDto;
import com.juno.appling.domain.member.record.JoinRecord;
import com.juno.appling.domain.member.record.LoginRecord;
import com.juno.appling.domain.member.service.MemberAuthService;
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
    public ResponseEntity<Api<JoinRecord>> join(@RequestBody @Validated JoinDto joinDto, BindingResult bindingResult){
        return ResponseEntity.status(HttpStatus.CREATED).body(Api.<JoinRecord>builder()
                .code(POST.code)
                .message(POST.message)
                .data(memberAuthService.join(joinDto))
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<Api<LoginRecord>> login(@RequestBody @Validated LoginDto loginDto, BindingResult bindingResult){
        return ResponseEntity.ok(
                Api.<LoginRecord>builder()
                        .code(SUCCESS.code)
                        .message(SUCCESS.message)
                        .data(memberAuthService.login(loginDto))
                        .build()
        );
    }

    @GetMapping("/refresh/{refresh_token}")
    public ResponseEntity<Api<LoginRecord>> refresh(@PathVariable(value = "refresh_token") String refreshToken){
        return ResponseEntity.ok(
                Api.<LoginRecord>builder()
                        .code(SUCCESS.code)
                        .message(SUCCESS.message)
                        .data(memberAuthService.refresh(refreshToken))
                        .build()
        );
    }
}
