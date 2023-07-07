package com.juno.appling.domain.member.vo;

import com.juno.appling.config.base.BaseVo;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class BuyerVo extends BaseVo {
    @NotNull
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String email;
    @NotNull
    private String tel;
    @NotNull
    private LocalDateTime createdAt;
    @NotNull
    private LocalDateTime modifiedAt;
}
