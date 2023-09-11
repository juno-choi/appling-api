package com.juno.appling.member.presentation;

import com.juno.appling.global.advice.exception.DuringProcessException;
import com.juno.appling.global.base.Api;
import com.juno.appling.global.base.MessageVo;
import com.juno.appling.member.dto.response.MemberResponse;
import com.juno.appling.member.dto.response.RecipientListResponse;
import com.juno.appling.member.application.MemberService;
import com.juno.appling.member.dto.request.*;
import com.juno.appling.product.domain.vo.SellerVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import static com.juno.appling.global.base.ResultCode.POST;
import static com.juno.appling.global.base.ResultCode.SUCCESS;

@RestController
@RequestMapping("${api-prefix}/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @GetMapping("")
    public ResponseEntity<Api<MemberResponse>> member(HttpServletRequest request) {
        return ResponseEntity.ok(
            Api.<MemberResponse>builder()
                .code(SUCCESS.code)
                .message(SUCCESS.message)
                .data(memberService.member(request))
                .build()
        );
    }

    @GetMapping("/seller")
    public ResponseEntity<Api<SellerVo>> getSeller(HttpServletRequest request) {
        return ResponseEntity.ok(
            Api.<SellerVo>builder()
                .code(SUCCESS.code)
                .message(SUCCESS.message)
                .data(memberService.getSeller(request))
                .build()
        );
    }

    @PostMapping("/seller")
    public ResponseEntity<Api<MessageVo>> postSeller(
            @RequestBody @Validated PostSellerRequest postSellerRequest, HttpServletRequest request,
            BindingResult bindingResult) {
        return ResponseEntity.ok(
            Api.<MessageVo>builder()
                .code(POST.code)
                .message(POST.message)
                .data(memberService.postSeller(postSellerRequest, request))
                .build()
        );
    }

    @PutMapping("/seller")
    public ResponseEntity<Api<MessageVo>> putSeller(
            @RequestBody @Validated PutSellerRequest putSellerRequest, HttpServletRequest request,
            BindingResult bindingResult) {
        return ResponseEntity.ok(
            Api.<MessageVo>builder()
                .code(SUCCESS.code)
                .message(SUCCESS.message)
                .data(memberService.putSeller(putSellerRequest, request))
                .build()
        );
    }

    @PatchMapping
    public ResponseEntity<Api<MessageVo>> patchMember(
            @RequestBody @Validated PatchMemberRequest patchMemberRequest, HttpServletRequest request,
            BindingResult bindingResult) {
        return ResponseEntity.ok(
            Api.<MessageVo>builder()
                .code(SUCCESS.code)
                .message(SUCCESS.message)
                .data(memberService.patchMember(patchMemberRequest, request))
                .build()
        );
    }

    @PostMapping("/recipient")
    public ResponseEntity<Api<MessageVo>> postRecipient(
            @RequestBody @Validated PostRecipientRequest postRecipientRequestInfo, HttpServletRequest request,
            BindingResult bindingResult) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Api.<MessageVo>builder()
                .code(POST.code)
                .message(POST.message)
                .data(memberService.postRecipient(postRecipientRequestInfo, request))
                .build()
            );
    }

    @GetMapping("/recipient")
    public ResponseEntity<Api<RecipientListResponse>> getRecipientList(HttpServletRequest request) {
        return ResponseEntity.ok(Api.<RecipientListResponse>builder()
            .code(SUCCESS.code)
            .message(SUCCESS.message)
            .data(memberService.getRecipient(request))
            .build()
        );
    }

    @PostMapping("/seller/introduce")
    public ResponseEntity<Api<MessageVo>> postIntroduce(
            @RequestBody @Validated PostIntroduceRequest postIntroduceRequest, HttpServletRequest request,
            BindingResult bindingResult) {
        return ResponseEntity.ok(Api.<MessageVo>builder()
            .code(SUCCESS.code)
            .message(SUCCESS.message)
            .data(memberService.postIntroduce(postIntroduceRequest, request))
            .build()
        );
    }

    @GetMapping("/seller/introduce")
    public void postIntroduce(HttpServletRequest request, HttpServletResponse response) {
        String introduce = memberService.getIntroduce(request);
        PrintWriter writer = null;
        try {
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType("text/html; charset=UTF-8");
            writer = response.getWriter();
            writer.print(introduce);
            writer.flush();
        } catch (IOException e) {
            throw new DuringProcessException("소개 페이지 반환 실패");
        } finally {
            if(writer != null){
                writer.close();
            }
        }

        response.setStatus(200);
    }
}
