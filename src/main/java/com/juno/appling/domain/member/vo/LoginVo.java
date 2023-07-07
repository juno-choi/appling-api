package com.juno.appling.domain.member.vo;

import com.juno.appling.config.base.BaseVo;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginVo extends BaseVo {
    private String type;
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpired;
    private Long refreshTokenExpired;
}
