package com.juno.appling.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2")
public class HelloV2Controller {
    @GetMapping("/hello")
    public String hello(){
        return "hello2";
    }
}
