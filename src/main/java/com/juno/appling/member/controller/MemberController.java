package com.juno.appling.member.controller;

import com.juno.appling.config.base.Api;
import com.juno.appling.config.base.MessageVo;
import com.juno.appling.member.domain.dto.*;
import com.juno.appling.member.domain.vo.MemberVo;
import com.juno.appling.member.domain.vo.RecipientListVo;
import com.juno.appling.member.service.MemberService;
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

import static com.juno.appling.config.base.ResultCode.POST;
import static com.juno.appling.config.base.ResultCode.SUCCESS;

@RestController
@RequestMapping("${api-prefix}/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @GetMapping("")
    public ResponseEntity<Api<MemberVo>> member(HttpServletRequest request) {
        return ResponseEntity.ok(
            Api.<MemberVo>builder()
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
        @RequestBody @Validated PostSellerDto postSellerDto, HttpServletRequest request,
        BindingResult bindingResult) {
        return ResponseEntity.ok(
            Api.<MessageVo>builder()
                .code(POST.code)
                .message(POST.message)
                .data(memberService.postSeller(postSellerDto, request))
                .build()
        );
    }

    @PutMapping("/seller")
    public ResponseEntity<Api<MessageVo>> putSeller(
        @RequestBody @Validated PutSellerDto putSellerDto, HttpServletRequest request,
        BindingResult bindingResult) {
        return ResponseEntity.ok(
            Api.<MessageVo>builder()
                .code(SUCCESS.code)
                .message(SUCCESS.message)
                .data(memberService.putSeller(putSellerDto, request))
                .build()
        );
    }

    @PatchMapping
    public ResponseEntity<Api<MessageVo>> patchMember(
        @RequestBody @Validated PatchMemberDto patchMemberDto, HttpServletRequest request,
        BindingResult bindingResult) {
        return ResponseEntity.ok(
            Api.<MessageVo>builder()
                .code(SUCCESS.code)
                .message(SUCCESS.message)
                .data(memberService.patchMember(patchMemberDto, request))
                .build()
        );
    }

    @PostMapping("/recipient")
    public ResponseEntity<Api<MessageVo>> postRecipient(
        @RequestBody @Validated PostRecipientDto postRecipientDtoInfo, HttpServletRequest request,
        BindingResult bindingResult) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Api.<MessageVo>builder()
                .code(POST.code)
                .message(POST.message)
                .data(memberService.postRecipient(postRecipientDtoInfo, request))
                .build()
            );
    }

    @GetMapping("/recipient")
    public ResponseEntity<Api<RecipientListVo>> getRecipientList(HttpServletRequest request) {
        return ResponseEntity.ok(Api.<RecipientListVo>builder()
            .code(SUCCESS.code)
            .message(SUCCESS.message)
            .data(memberService.getRecipient(request))
            .build()
        );
    }

    @PostMapping("/seller/introduce")
    public ResponseEntity<Api<MessageVo>> postIntroduce(
        @RequestBody @Validated PostIntroduceDto postIntroduceDto, HttpServletRequest request,
        BindingResult bindingResult) {
        return ResponseEntity.ok(Api.<MessageVo>builder()
            .code(SUCCESS.code)
            .message(SUCCESS.message)
            .data(memberService.postIntroduce(postIntroduceDto, request))
            .build()
        );
    }

    @GetMapping("/seller/introduce")
    public void postIntroduce(HttpServletRequest request, HttpServletResponse response) {
        String introduce = memberService.getIntroduce(request);
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.print(introduce);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            writer.close();
        }
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/html; charset=UTF-8");

        //TODO 지워야됨
        log.info(introduce);
        response.setStatus(200);
    }
}
