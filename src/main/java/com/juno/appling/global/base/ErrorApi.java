package com.juno.appling.global.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ErrorApi<T> {

    private String code;
    private String message;
    private List<T> errors;
}
