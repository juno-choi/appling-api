package com.juno.appling.member.domain.model;

import com.juno.appling.member.controller.response.MemberResponse;
import com.juno.appling.member.enums.MemberStatus;
import com.juno.appling.member.enums.MemberRole;
import com.juno.appling.member.enums.SnsJoinType;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Member {
    private Long id;
    private String email;
    private String password;
    private String nickname;
    private String name;
    private String birth;
    private MemberRole role;
    private String snsId;
    private SnsJoinType snsType;
    private MemberStatus status;
    private List<Recipient> recipientList = new LinkedList<>();
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @Builder
    public Member(Long id, String email, String password, String nickname, String name,
        String birth,
        MemberRole role, String snsId, SnsJoinType snsType, MemberStatus status,
        List<Recipient> recipientList, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.birth = birth;
        this.role = role;
        this.snsId = snsId;
        this.snsType = snsType;
        this.status = status;
        this.recipientList = recipientList;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public MemberResponse toResponse(){
        return MemberResponse.builder()
            .memberId(id)
            .email(email)
            .nickname(nickname)
            .name(name)
            .role(role)
            .snsType(snsType)
            .status(status)
            .createdAt(createdAt)
            .modifiedAt(modifiedAt)
            .build();
    }
}
