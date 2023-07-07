package com.juno.appling.domain.member.vo;

import com.juno.appling.config.base.BaseVo;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class JoinVo extends BaseVo {
    private String name;
    private String nickname;
    private String email;
}
