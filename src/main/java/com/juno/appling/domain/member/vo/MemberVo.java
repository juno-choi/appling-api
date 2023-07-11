package com.juno.appling.domain.member.vo;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.domain.member.enums.Role;
import com.juno.appling.domain.member.enums.SnsJoinType;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record MemberVo(Long memberId, String email, String nickname, String name, Role role, SnsJoinType snsType, LocalDateTime createdAt, LocalDateTime modifiedAt) {
}
