package com.jms.alertmessaging.exception.auth;


/**
 * 이메일이 이미 사용 중인 경우
 * */
public class EmailAlreadyUsedException extends RuntimeException {
    public EmailAlreadyUsedException(String message) {
        super(message);
    }
}
