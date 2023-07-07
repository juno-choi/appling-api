package com.juno.appling;

public enum CommonTest {
    MEMBER_EMAIL("member@appling.com"),
    SELLER_EMAIL("seller@appling.com"),
    SELLER2_EMAIL("seller2@appling.com"),
    PASSWORD("password"),
    ;
    private String val;

    CommonTest(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }
}
