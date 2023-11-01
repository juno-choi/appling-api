package com.juno.appling.global.base;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorDto {

    @NotNull(message = "point 비어있을 수 없습니다.")
    private String point;
    @NotNull(message = "detail 비어있을 수 없습니다.")
    private String detail;
}
