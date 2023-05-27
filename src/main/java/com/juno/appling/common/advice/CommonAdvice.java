package com.juno.appling.common.advice;

import com.juno.appling.domain.dto.ErrorApi;
import com.juno.appling.domain.enums.ResultCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice(basePackages = "com.juno.appling")
public class CommonAdvice {
    @ExceptionHandler
    public ResponseEntity<ErrorApi<String>> missingServletRequestParameterException(MissingServletRequestParameterException e){
        List<String> errors = new ArrayList<>();
        errors.add(String.format("please check parameter : %s (%s)", e.getParameterName(), e.getParameterType()));

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
