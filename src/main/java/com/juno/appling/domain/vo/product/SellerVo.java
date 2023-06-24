package com.juno.appling.domain.vo.product;

import com.juno.appling.domain.vo.BaseVo;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SellerVo extends BaseVo {
    @NotNull
    private Long memberId;
    @NotNull
    private String email;
    @NotNull
    private String nickname;
    @NotNull
    private String name;
}
