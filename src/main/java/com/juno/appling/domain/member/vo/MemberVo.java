package com.juno.appling.domain.member.vo;


import com.juno.appling.domain.member.enums.Role;
import com.juno.appling.domain.member.enums.SnsJoinType;
import com.juno.appling.config.base.BaseVo;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberVo extends BaseVo {
    private Long memberId;
    private String email;
    private String nickname;
    private String name;
    private Role role;
    private SnsJoinType snsType;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
