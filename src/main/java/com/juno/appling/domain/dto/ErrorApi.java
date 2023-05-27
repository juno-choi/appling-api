package com.juno.appling.domain.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ErrorApi<T> {
    private String code;
    private String message;
    private List<T> errors;
}
