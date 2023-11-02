package com.juno.appling.member.domain.entity;

import com.juno.appling.member.controller.request.JoinRequest;
import com.juno.appling.member.domain.model.Member;
import com.juno.appling.member.enums.MemberRole;
import com.juno.appling.member.enums.MemberStatus;
import com.juno.appling.member.enums.SnsJoinType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "members")
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @NotNull
    @Column(unique = true)
    private String email;
    @NotNull
    private String password;
    @NotNull
    private String nickname;
    @NotNull
    private String name;
    private String birth;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    private String snsId;

    @Enumerated(EnumType.STRING)
    private SnsJoinType snsType;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<RecipientEntity> recipientList = new LinkedList<>();

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static MemberEntity from(Member member) {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.id = member.getId();
        memberEntity.email = member.getEmail();
        memberEntity.password = member.getPassword();
        memberEntity.nickname = member.getNickname();
        memberEntity.name = member.getName();
        memberEntity.birth = member.getBirth();
        memberEntity.role = member.getRole();
        memberEntity.snsId = member.getSnsId();
        memberEntity.snsType = member.getSnsType();
        memberEntity.status = member.getStatus();
        memberEntity.createdAt = member.getCreatedAt();
        memberEntity.modifiedAt = member.getModifiedAt();
        return memberEntity;
    }

    public Member toModel() {
        return Member.builder()
            .id(id)
            .email(email)
            .password(password)
            .nickname(nickname)
            .name(name)
            .birth(birth)
            .role(role)
            .snsId(snsId)
            .snsType(snsType)
            .status(status)
            .createdAt(createdAt)
            .modifiedAt(modifiedAt)
            .build();
    }

    public MemberEntity(@NotNull String email, @NotNull String password, @NotNull String nickname,
        @NotNull String name, String birth, MemberRole role, String snsId, SnsJoinType snsType,
        MemberStatus status, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.birth = birth;
        this.role = role;
        this.snsId = snsId;
        this.snsType = snsType;
        this.status = status;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public MemberEntity(Long id, @NotNull String email, @NotNull String password,
        @NotNull String nickname, @NotNull String name, String birth, MemberRole role, String snsId,
        SnsJoinType snsType, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.birth = birth;
        this.role = role;
        this.snsId = snsId;
        this.snsType = snsType;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static MemberEntity of(JoinRequest joinRequest) {
        LocalDateTime now = LocalDateTime.now();
        return new MemberEntity(joinRequest.getEmail(), joinRequest.getPassword(), joinRequest.getNickname(),
            joinRequest.getName(), joinRequest.getBirth(), MemberRole.MEMBER, null, null, MemberStatus.NORMAL, now,
            now);
    }

    public static MemberEntity of(JoinRequest joinRequest, String snsId, SnsJoinType snsType) {
        LocalDateTime now = LocalDateTime.now();
        return new MemberEntity(joinRequest.getEmail(), joinRequest.getPassword(), joinRequest.getNickname(),
            joinRequest.getName(), joinRequest.getBirth(), MemberRole.MEMBER, snsId, snsType, MemberStatus.NORMAL, now,
            now);
    }

    public void patchMember(String birth, String name, String nickname, String password) {
        LocalDateTime now = LocalDateTime.now();
        if (!birth.isEmpty()) {
            this.birth = birth;
            this.modifiedAt = now;
        }
        if (!name.isEmpty()) {
            this.name = name;
            this.modifiedAt = now;
        }
        if (!nickname.isEmpty()) {
            this.nickname = nickname;
            this.modifiedAt = now;
        }
        if (!password.isEmpty()) {
            this.password = password;
            this.modifiedAt = now;
        }
    }

    public void patchMemberRole(MemberRole memberRole) {
        this.role = memberRole;
    }

}
