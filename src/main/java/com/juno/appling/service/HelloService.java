package com.juno.appling.service;

import com.juno.appling.domain.vo.MessageVo;
import org.springframework.stereotype.Service;

@Service
public class HelloService {
    public MessageVo hello(){
        return MessageVo.builder().message("테스트").build();
    }
}
