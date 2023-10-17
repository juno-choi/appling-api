package com.juno.appling.member.domain;

import com.juno.appling.member.dto.request.JoinRequest;
import com.juno.appling.member.enums.Role;
import com.juno.appling.member.enums.SnsJoinType;
import com.juno.appling.member.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.LinkedList;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Member {

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
    private Role role;

    private String snsId;

    @Enumerated(EnumType.STRING)
    private SnsJoinType snsType;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Recipient> recipientList = new LinkedList<>();

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public Member(@NotNull String email, @NotNull String password, @NotNull String nickname,
        @NotNull String name, String birth, Role role, String snsId, SnsJoinType snsType,
        Status status, LocalDateTime createdAt, LocalDateTime modifiedAt) {
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

    public Member(Long id, @NotNull String email, @NotNull String password,
        @NotNull String nickname, @NotNull String name, String birth, Role role, String snsId,
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

    public static Member of(JoinRequest joinRequest) {
        LocalDateTime now = LocalDateTime.now();
        return new Member(joinRequest.getEmail(), joinRequest.getPassword(), joinRequest.getNickname(),
            joinRequest.getName(), joinRequest.getBirth(), Role.MEMBER, null, null, Status.NORMAL, now,
            now);
    }

    public static Member of(JoinRequest joinRequest, String snsId, SnsJoinType snsType) {
        LocalDateTime now = LocalDateTime.now();
        return new Member(joinRequest.getEmail(), joinRequest.getPassword(), joinRequest.getNickname(),
            joinRequest.getName(), joinRequest.getBirth(), Role.MEMBER, snsId, snsType, Status.NORMAL, now,
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

    public void patchMemberRole(Role role) {
        this.role = role;
    }

}
