package com.juno.appling.member.controller.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.juno.appling.member.domain.entity.MemberEntity;
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

    public static MemberResponse from(MemberEntity memberEntity) {
        return MemberResponse.builder()
                .memberId(memberEntity.getId())
                .email(memberEntity.getEmail())
                .nickname(memberEntity.getNickname())
                .name(memberEntity.getName())
                .role(memberEntity.getRole())
                .snsType(memberEntity.getSnsType())
                .status(memberEntity.getStatus())
                .createdAt(memberEntity.getCreatedAt())
                .modifiedAt(memberEntity.getModifiedAt())
                .build();
    }
}
