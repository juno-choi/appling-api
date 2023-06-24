package com.juno.appling.controller.member;

import com.juno.appling.domain.dto.Api;
import com.juno.appling.domain.vo.MessageVo;
import com.juno.appling.domain.vo.member.MemberVo;
import com.juno.appling.service.member.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.juno.appling.domain.enums.ResultCode.SUCCESS;

@RestController
@RequestMapping("${api-prefix}/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("")
    public ResponseEntity<Api<MemberVo>> member(HttpServletRequest request){
        return ResponseEntity.ok(
                Api.<MemberVo>builder()
                        .code(SUCCESS.code)
                        .message(SUCCESS.message)
                        .data(memberService.member(request))
                        .build()
        );
    }

    @PostMapping("/seller")
    public ResponseEntity<Api<MessageVo>> applySeller(HttpServletRequest request){
        return ResponseEntity.ok(
                Api.<MessageVo>builder()
                        .code(SUCCESS.code)
                        .message(SUCCESS.message)
                        .data(memberService.applySeller(request))
                        .build()
        );
    }
}
