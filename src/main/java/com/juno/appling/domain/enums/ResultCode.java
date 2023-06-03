package com.juno.appling.domain.enums;

public enum ResultCode {
    SUCCESS("0000", "success"),
    POST("0001", "201 success"),
    ;

    ResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code;
    public String message;
}
