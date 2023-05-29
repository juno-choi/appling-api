package com.juno.appling.domain.vo.member;


import com.juno.appling.domain.enums.member.Role;
import com.juno.appling.domain.vo.BaseVo;
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
    private String snsType;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
