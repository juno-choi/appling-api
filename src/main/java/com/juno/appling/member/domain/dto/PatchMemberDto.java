package com.juno.appling.member.domain.dto;

import com.juno.appling.member.domain.enums.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchMemberDto {
    private String birth;
    private String name;
    private String password;
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Status status;
}
