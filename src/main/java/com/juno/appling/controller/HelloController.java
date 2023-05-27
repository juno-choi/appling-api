package com.juno.appling.controller;

import com.juno.appling.domain.dto.Api;
import com.juno.appling.domain.dto.TestDto;
import com.juno.appling.domain.vo.MessageVo;
import com.juno.appling.service.HelloService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/validation")
    public ResponseEntity<Api<MessageVo>> validation(@RequestBody @Validated TestDto testDto, BindingResult bindingResult){
        return ResponseEntity.ok(Api.<MessageVo>builder()
                .code(SUCCESS.CODE)
                .message(SUCCESS.MESSAGE)
                .data(helloService.hello())
                .build());
    }

    @GetMapping("/validation")
    public ResponseEntity<Api<MessageVo>> validation2(@RequestParam(name = "id") Long id){
        return ResponseEntity.ok(Api.<MessageVo>builder()
                .code(SUCCESS.CODE)
                .message(SUCCESS.MESSAGE)
                .data(helloService.hello())
                .build());
    }
}
