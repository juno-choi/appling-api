package com.juno.appling.common.presentation;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import com.juno.appling.common.dto.response.UploadResponse;
import com.juno.appling.common.application.CommonS3Service;
import com.juno.appling.global.advice.exception.DuringProcessException;
import com.juno.appling.global.base.Api;
import com.juno.appling.global.base.ResultCode;
import com.juno.appling.member.application.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("${api-prefix}/common")
@RequiredArgsConstructor
@Slf4j
public class CommonController {

    private final CommonS3Service commonS3Service;
    private final MemberService memberService;

    @PostMapping("/image")
    public ResponseEntity<Api<UploadResponse>> uploadImage(@RequestPart List<MultipartFile> image,
                                                           HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.SC_CREATED)
            .body(
                new Api<>(ResultCode.SUCCESS.code, ResultCode.SUCCESS.message, commonS3Service.s3UploadFile(image, "image/%s/%s/", request))
            );
    }


    @PostMapping("/html")
    public ResponseEntity<Api<UploadResponse>> uploadHtml(@RequestPart List<MultipartFile> html,
                                                          HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.SC_CREATED)
            .body(new Api<>(ResultCode.POST.code, ResultCode.POST.message, commonS3Service.s3UploadFile(html, "html/%s/%s/", request))
        );
    }

    @GetMapping("/introduce/{seller_id}")
    public void introduceBySellerId(@PathVariable(name = "seller_id") Long sellerId, HttpServletResponse response){
        String introduce = memberService.getIntroduce(sellerId);
        PrintWriter writer = null;
        try {
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType("text/html; charset=UTF-8");
            writer = response.getWriter();
            writer.print(introduce);
            writer.flush();
        } catch (IOException e) {
            log.error("[introduce error]", e);
            throw new DuringProcessException("소개 페이지 반환 실패", e);
        } finally {
            if(writer != null){
                writer.close();
            }
        }

        response.setStatus(200);
    }
}
