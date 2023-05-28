package com.juno.appling.controller.member;

import com.juno.appling.domain.dto.Api;
import com.juno.appling.domain.dto.member.JoinDto;
import com.juno.appling.domain.dto.member.LoginDto;
import com.juno.appling.domain.vo.member.JoinVo;
import com.juno.appling.domain.vo.member.LoginVo;
import com.juno.appling.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.juno.appling.domain.enums.ResultCode.*;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<Api<JoinVo>> join(@RequestBody @Validated JoinDto joinDto, BindingResult bindingResult){
        return ResponseEntity.status(HttpStatus.CREATED).body(Api.<JoinVo>builder()
                .code(POST.CODE)
                .message(POST.MESSAGE)
                .data(memberService.join(joinDto))
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<Api<LoginVo>> login(@RequestBody @Validated LoginDto loginDto, BindingResult bindingResult){
        return ResponseEntity.ok(
                Api.<LoginVo>builder()
                        .code(SUCCESS.CODE)
                        .message(SUCCESS.MESSAGE)
                        .data(memberService.login(loginDto))
                        .build()
        );
    }
}
