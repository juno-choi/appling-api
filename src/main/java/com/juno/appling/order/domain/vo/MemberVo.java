package com.juno.appling.order.domain.vo;

import com.juno.appling.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
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
