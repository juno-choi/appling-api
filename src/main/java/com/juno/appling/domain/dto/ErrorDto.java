package com.juno.appling.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorDto {
    @NotNull(message = "point 비어있을 수 없습니다.")
    private String point;
    @NotNull(message = "detail 비어있을 수 없습니다.")
    private String detail;
}
