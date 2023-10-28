package com.juno.appling;

import com.juno.appling.member.controller.response.LoginResponse;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

public class Base {
    public static final ObjectMapper objectMapper = new ObjectMapper();
    public static final String MEMBER_EMAIL = "member@appling.com";
    public static final String SELLER_EMAIL = "seller@appling.com";
    public static final String SELLER2_EMAIL = "seller2@appling.com";
    public static final String PASSWORD = "password";
    public static final LoginResponse MEMBER_LOGIN = new LoginResponse("", "member-test-token", "refresh-token", 1000L, 1000L);
    public static final LoginResponse SELLER_LOGIN = new LoginResponse("", "seller-test-token", "refresh-token", 1000L, 1000L);
    public static final LoginResponse SELLER2_LOGIN = new LoginResponse("", "seller2-test-token", "refresh-token", 1000L, 1000L);
    public static final Long CATEGORY_ID_FRUIT = 1L;
    public static final Long CATEGORY_ID_VEGETABLE = 2L;

    public static final Long PRODUCT_ID_APPLE = 1L;
    public static final Long PRODUCT_OPTION_ID_APPLE = 1L;
    public static final Long PRODUCT_ID_PEAR = 2L;
    public static final Long PRODUCT_OPTION_ID_PEAR = 2L;
    public static final Long PRODUCT_ID_NORMAL = 3L;

}
