package com.juno.appling.domain.enums.member;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {
    MEMBER("MEMBER", "MEMBER"),
    SELLER("SELLER", "MEMBER, SELLER"),
    ADMIN("ADMIN", "MEMBER, SELLER, ADMIN"),
    ;
    public final String role;
    public final String roleList;
}
