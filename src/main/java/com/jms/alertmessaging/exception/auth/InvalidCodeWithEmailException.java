package com.jms.alertmessaging.exception.auth;


/**
 * 인증 코드와 이메일이 유효하지 않은 경우
 * */
public class InvalidCodeWithEmailException extends RuntimeException {
    public InvalidCodeWithEmailException(String message) {
        super(message);
    }
}
