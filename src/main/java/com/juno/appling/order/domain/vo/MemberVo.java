package com.juno.appling.order.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MemberVo {
    private String email;
    private String nickname;
    private String name;
    private String birth;

    public static MemberVo fromMemberEntity(Member member){
        return MemberVo.builder()
            .email(member.getEmail())
            .name(member.getName())
            .nickname(member.getNickname())
            .birth(member.getBirth())
            .build();
    }
}
