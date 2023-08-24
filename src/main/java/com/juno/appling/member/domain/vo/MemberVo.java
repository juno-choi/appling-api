package com.juno.appling.member.domain.vo;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.member.domain.enums.Role;
import com.juno.appling.member.domain.enums.SnsJoinType;
import com.juno.appling.member.domain.enums.Status;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record MemberVo(
    Long memberId,
    String email,
    String nickname,
    String name,
    Role role,
    SnsJoinType snsType,
    Status status,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt
) {

}
