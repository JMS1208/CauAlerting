package com.jms.alertmessaging.exception.auth;

public class CookieNotFoundException extends RuntimeException {

    public CookieNotFoundException(String message) {
        super(message);
    }
}
