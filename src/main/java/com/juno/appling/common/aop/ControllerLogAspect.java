package com.juno.appling.common.aop;

import com.juno.appling.domain.dto.ErrorApi;
import com.juno.appling.domain.enums.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;

@Component
@Aspect
@Slf4j
public class ControllerLogAspect {
    @Around("execution(* com.juno.appling.controller..*.*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        String type = pjp.getSignature().getDeclaringTypeName();
        String method = pjp.getSignature().getName();
        String requestURI = ((ServletRequestAttributes) requestAttributes).getRequest().getRequestURI();

        log.info("[appling] validation check... requestUri=[{}] package = [{}], method = [{}]", requestURI, type, method);

        Object[] args = pjp.getArgs();
        for(Object a : args){
            if(a instanceof BindingResult){   // object type == BindingResult
                BindingResult bindingResult = (BindingResult) a;
                if(bindingResult.hasErrors()){  // 유효성 검사에 걸리는 에러가 존재한다면

                    List<String> errors = new ArrayList<>();
                    for(FieldError error : bindingResult.getFieldErrors()){
                        log.warn("[parameter : {}] [message = {}]", error.getField(), error.getDefaultMessage());
                        errors.add(String.format("%s", error.getDefaultMessage()));
                    }

                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(
                                    ErrorApi.<String>builder()
                                            .code(ResultCode.BAD_REQUEST.CODE)
                                            .message(ResultCode.BAD_REQUEST.MESSAGE)
                                            .errors(errors)
                                            .build()
                            );
                }
            }
        }

        return pjp.proceed();
    }
}
