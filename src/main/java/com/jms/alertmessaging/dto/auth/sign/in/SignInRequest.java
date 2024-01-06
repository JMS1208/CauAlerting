package com.jms.alertmessaging.dto.auth.sign.in;

import lombok.Data;

@Data
public class SignInRequest {
    private String email;
    private String password;
}
