package com.juno.appling.domain.enums.member;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {
    MEMBER("MEMBER", "일반 유저"),
    SELLER("SELLER", "판매자 유저"),
    ;
    public final String ROLE;
    public final String ROLE_VALUE;
}
