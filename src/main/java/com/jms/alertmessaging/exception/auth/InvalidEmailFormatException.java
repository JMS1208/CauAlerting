package com.jms.alertmessaging.exception.auth;


/**
 *  이메일 형식이 맞지 않는 경우
 * */
public class InvalidEmailFormatException extends RuntimeException {

    public InvalidEmailFormatException(String message) {
        super(message);
    }
}
