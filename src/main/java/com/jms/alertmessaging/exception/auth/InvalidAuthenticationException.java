package com.jms.alertmessaging.exception.auth;

import org.springframework.security.core.AuthenticationException;

//인증되지 않은 사용자일 경우
public class InvalidAuthenticationException extends AuthenticationException {
    public InvalidAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public InvalidAuthenticationException(String msg) {
        super(msg);
    }
}