package com.juno.appling.order.controller.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.member.domain.entity.MemberEntity;
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

    public static MemberVo fromMemberEntity(MemberEntity memberEntity){
        return MemberVo.builder()
            .email(memberEntity.getEmail())
            .name(memberEntity.getName())
            .nickname(memberEntity.getNickname())
            .birth(memberEntity.getBirth())
            .build();
    }
}
