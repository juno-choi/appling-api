package com.juno.appling.domain.entity.member;

import com.juno.appling.domain.dto.member.JoinDto;
import com.juno.appling.domain.enums.member.Role;
import com.juno.appling.domain.enums.member.SnsJoinType;
import com.juno.appling.domain.enums.member.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    private BuyerInfo buyerInfo;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<RecipientInfo> buyerInfoList = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public Member(@NotNull String email, @NotNull String password, @NotNull String nickname, @NotNull String name, String birth, Role role, String snsId, SnsJoinType snsType, LocalDateTime createdAt, LocalDateTime modifiedAt) {
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

    public Member(Long id, @NotNull String email, @NotNull String password, @NotNull String nickname, @NotNull String name, String birth, Role role, String snsId, SnsJoinType snsType, LocalDateTime createdAt, LocalDateTime modifiedAt) {
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

    public static Member of(JoinDto joinDto){
        LocalDateTime now = LocalDateTime.now();
        return new Member(joinDto.getEmail(), joinDto.getPassword(), joinDto.getNickname(), joinDto.getName(), joinDto.getBirth(), Role.MEMBER, null, null, now, now);
    }

    public static Member of(JoinDto joinDto, String snsId, SnsJoinType snsType){
        LocalDateTime now = LocalDateTime.now();
        return new Member(joinDto.getEmail(), joinDto.getPassword(), joinDto.getNickname(), joinDto.getName(), joinDto.getBirth(), Role.MEMBER, snsId, snsType, now, now);
    }

    public void patchMember(String birth, String name, String nickname, String password){
        LocalDateTime now = LocalDateTime.now();
        if(!birth.isEmpty()){
            this.birth = birth;
            this.modifiedAt = now;
        }
        if(!name.isEmpty()){
            this.name = name;
            this.modifiedAt = now;
        }
        if(!nickname.isEmpty()){
            this.nickname = nickname;
            this.modifiedAt = now;
        }
        if(!password.isEmpty()){
            this.password = password;
            this.modifiedAt = now;
        }
    }

    public void patchMemberRole(Role role){
        this.role = role;
    }

}
