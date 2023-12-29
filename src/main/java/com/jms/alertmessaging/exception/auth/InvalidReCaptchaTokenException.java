package com.jms.alertmessaging.exception.auth;

/**
 * 리캡챠 토큰이 유효하지 않은 경우
 * */
public class InvalidReCaptchaTokenException extends RuntimeException {

    public InvalidReCaptchaTokenException(String message) {
        super(message);
    }
}
