package com.juno.appling.controller.member;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api-prefix}/v1")
public class HelloController {
    @GetMapping("/hello")
    public String hello(){
        return "hello";
    }
}
