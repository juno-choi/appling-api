package com.juno.appling.global.advice.exception;

public class DuringProcessException extends RuntimeException{


    public DuringProcessException(String message, Throwable throwable) {
        super(message, throwable);
    }
    public DuringProcessException(String message) {
        super(message);
    }
}
