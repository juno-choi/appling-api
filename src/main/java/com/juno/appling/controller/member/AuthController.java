package com.juno.appling.controller.member;

import com.juno.appling.domain.dto.Api;
import com.juno.appling.domain.dto.member.JoinDto;
import com.juno.appling.domain.dto.member.LoginDto;
import com.juno.appling.domain.vo.member.JoinVo;
import com.juno.appling.domain.vo.member.LoginVo;
import com.juno.appling.service.member.MemberAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.juno.appling.domain.enums.ResultCode.POST;
import static com.juno.appling.domain.enums.ResultCode.SUCCESS;

@RestController
@RequestMapping("${api-prefix}/auth")
@RequiredArgsConstructor
public class AuthController {
    private final MemberAuthService memberAuthService;

    @PostMapping("/join")
    public ResponseEntity<Api<JoinVo>> join(@RequestBody @Validated JoinDto joinDto, BindingResult bindingResult){
        return ResponseEntity.status(HttpStatus.CREATED).body(Api.<JoinVo>builder()
                .code(POST.CODE)
                .message(POST.MESSAGE)
                .data(memberAuthService.join(joinDto))
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<Api<LoginVo>> login(@RequestBody @Validated LoginDto loginDto, BindingResult bindingResult){
        return ResponseEntity.ok(
                Api.<LoginVo>builder()
                        .code(SUCCESS.CODE)
                        .message(SUCCESS.MESSAGE)
                        .data(memberAuthService.login(loginDto))
                        .build()
        );
    }

    @GetMapping("/refresh/{refresh_token}")
    public ResponseEntity<Api<LoginVo>> refresh(@PathVariable(value = "refresh_token") String refreshToken){
        return ResponseEntity.ok(
                Api.<LoginVo>builder()
                        .code(SUCCESS.CODE)
                        .message(SUCCESS.MESSAGE)
                        .data(memberAuthService.refresh(refreshToken))
                        .build()
        );
    }
}
