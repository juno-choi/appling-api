package com.juno.appling.member.controller.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.member.domain.Member;
import com.juno.appling.member.enums.Role;
import com.juno.appling.member.enums.SnsJoinType;
import com.juno.appling.member.enums.Status;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@Getter
@Builder
public class MemberResponse {
    private Long memberId;
    private String email;
    private String nickname;
    private String name;
    private Role role;
    private SnsJoinType snsType;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .name(member.getName())
                .role(member.getRole())
                .snsType(member.getSnsType())
                .status(member.getStatus())
                .createdAt(member.getCreatedAt())
                .modifiedAt(member.getModifiedAt())
                .build();
    }
}
