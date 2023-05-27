package com.juno.appling.domain.vo.member;

import com.juno.appling.domain.vo.BaseVo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JoinVo extends BaseVo {
    private String name;
    private String nickname;
    private String email;
}
