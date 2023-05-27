package com.juno.appling.domain.entity.member;

import com.juno.appling.domain.enums.member.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Member {
    @Id
    @GeneratedValue
    @Column(name = "member_id")
    @NotNull
    private Long id;

    @NotNull
    private String password;
    @NotNull
    private String email;
    @NotNull
    private String nickname;
    @NotNull
    private String name;
    private String birth;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String snsId;
    private String snsType;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime modifiedAt = LocalDateTime.now();

    @Builder
    public Member(@NotNull Long id, @NotNull String password, @NotNull String email, @NotNull String nickname, @NotNull String name, String birth, Role role, String snsId, String snsType) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.name = name;
        this.birth = birth;
        this.role = role;
        this.snsId = snsId;
        this.snsType = snsType;
    }
}
