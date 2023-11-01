package com.juno.appling.member.controller;

import static com.juno.appling.global.base.ResultCode.POST;
import static com.juno.appling.global.base.ResultCode.SUCCESS;

import com.juno.appling.global.advice.exception.DuringProcessException;
import com.juno.appling.global.base.Api;
import com.juno.appling.global.base.MessageVo;
import com.juno.appling.member.controller.request.PatchMemberRequest;
import com.juno.appling.member.controller.request.PostIntroduceRequest;
import com.juno.appling.member.controller.request.PostRecipientRequest;
import com.juno.appling.member.controller.request.PostSellerRequest;
import com.juno.appling.member.controller.request.PutSellerRequest;
import com.juno.appling.member.controller.response.MemberResponse;
import com.juno.appling.member.controller.response.RecipientListResponse;
import com.juno.appling.member.service.MemberService;
import com.juno.appling.product.controller.response.SellerResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api-prefix}/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @GetMapping("")
    public ResponseEntity<Api<MemberResponse>> member(HttpServletRequest request) {
        return ResponseEntity.ok(
            new Api<>(SUCCESS.code, SUCCESS.message, memberService.member(request))
        );
    }

    @GetMapping("/seller")
    public ResponseEntity<Api<SellerResponse>> getSeller(HttpServletRequest request) {
        return ResponseEntity.ok(
            new Api<>(SUCCESS.code, SUCCESS.message, memberService.getSeller(request))
        );
    }

    @PostMapping("/seller")
    public ResponseEntity<Api<MessageVo>> postSeller(
            @RequestBody @Validated PostSellerRequest postSellerRequest, HttpServletRequest request,
            BindingResult bindingResult) {
        return ResponseEntity.ok(
            new Api<>(POST.code, POST.message, memberService.postSeller(postSellerRequest, request))
        );
    }

    @PutMapping("/seller")
    public ResponseEntity<Api<MessageVo>> putSeller(
            @RequestBody @Validated PutSellerRequest putSellerRequest, HttpServletRequest request,
            BindingResult bindingResult) {
        return ResponseEntity.ok(
            new Api<>(SUCCESS.code, SUCCESS.message, memberService.putSeller(putSellerRequest, request))
        );
    }

    @PatchMapping
    public ResponseEntity<Api<MessageVo>> patchMember(
            @RequestBody @Validated PatchMemberRequest patchMemberRequest, HttpServletRequest request,
            BindingResult bindingResult) {
        return ResponseEntity.ok(
            new Api<>(SUCCESS.code, SUCCESS.message, memberService.patchMember(patchMemberRequest, request))
        );
    }

    @PostMapping("/recipient")
    public ResponseEntity<Api<MessageVo>> postRecipient(
            @RequestBody @Validated PostRecipientRequest postRecipientRequestInfo, HttpServletRequest request,
            BindingResult bindingResult) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(
                new Api<>(POST.code, POST.message, memberService.postRecipient(postRecipientRequestInfo, request))
            );
    }

    @GetMapping("/recipient")
    public ResponseEntity<Api<RecipientListResponse>> getRecipientList(HttpServletRequest request) {
        return ResponseEntity.ok(
            new Api<>(SUCCESS.code, SUCCESS.message, memberService.getRecipient(request))
        );
    }

    @PostMapping("/seller/introduce")
    public ResponseEntity<Api<MessageVo>> postIntroduce(
            @RequestBody @Validated PostIntroduceRequest postIntroduceRequest, HttpServletRequest request,
            BindingResult bindingResult) {
        return ResponseEntity.ok(
            new Api<>(SUCCESS.code, SUCCESS.message, memberService.postIntroduce(postIntroduceRequest, request))
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
            throw new DuringProcessException("소개 페이지 반환 실패", e);
        } finally {
            if(writer != null){
                writer.close();
            }
        }

        response.setStatus(200);
    }
}
