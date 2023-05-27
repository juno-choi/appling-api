package com.juno.appling.controller;

import com.juno.appling.domain.dto.Api;
import com.juno.appling.domain.vo.MessageVo;
import com.juno.appling.service.HelloService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.juno.appling.domain.enums.ResultCode.*;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class HelloController {
    private final HelloService helloService;

    @GetMapping("/hello")
    public ResponseEntity<Api<MessageVo>> hello(){
        return ResponseEntity.ok(Api.<MessageVo>builder()
                .code(SUCCESS.CODE)
                .message(SUCCESS.MESSAGE)
                .data(helloService.hello())
                .build());
    }
}
