package com.juno.appling.controller.member;

import com.juno.appling.domain.dto.Api;
import com.juno.appling.domain.vo.member.MemberVo;
import com.juno.appling.service.member.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.juno.appling.domain.enums.ResultCode.*;

@RestController
@RequestMapping("${api-prefix}/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("")
    public ResponseEntity<Api<MemberVo>> member(HttpServletRequest request){
        return ResponseEntity.ok(
                Api.<MemberVo>builder()
                        .code(SUCCESS.CODE)
                        .message(SUCCESS.MESSAGE)
                        .data(memberService.member(request))
                        .build()
        );
    }
}
