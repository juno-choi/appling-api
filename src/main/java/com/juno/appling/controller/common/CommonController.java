package com.juno.appling.controller.common;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import com.juno.appling.domain.dto.Api;
import com.juno.appling.domain.enums.ResultCode;
import com.juno.appling.domain.vo.common.UploadVo;
import com.juno.appling.service.common.CommonS3Service;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/common")
@RequiredArgsConstructor
public class CommonController {
    private final CommonS3Service commonS3Service;

    @PostMapping("/image")
    public ResponseEntity<Api<UploadVo>> uploadImage(@RequestPart List<MultipartFile> image, HttpServletRequest request){
        return ResponseEntity.status(HttpStatus.SC_CREATED)
                .body(Api.<UploadVo>builder()
                        .code(ResultCode.SUCCESS.code)
                        .message(ResultCode.SUCCESS.message)
                        .data(commonS3Service.uploadImage(image, request))
                        .build());
    }
}
