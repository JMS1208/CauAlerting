package com.jms.alertmessaging.exception.auth;

public class SignInFailedException extends RuntimeException {
    public SignInFailedException(String message) {
        super(message);
    }
}
