package com.jms.alertmessaging.exception.auth;

public class SignUpFailedException extends RuntimeException {
    public SignUpFailedException(String message) {
        super(message);
    }
}
