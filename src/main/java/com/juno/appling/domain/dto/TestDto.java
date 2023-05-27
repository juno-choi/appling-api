package com.juno.appling.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestDto {
    @NotNull(message = "id는 필수 값입니다.")
    private String id;
    @NotNull(message = "age는 필수 값입니다.")
    @Min(value = 10, message = "pw 길이는 최소 10입니다.")
    @Max(value = 100, message = "pw 길이는 최대 100입니다.")
    private String age;
}
