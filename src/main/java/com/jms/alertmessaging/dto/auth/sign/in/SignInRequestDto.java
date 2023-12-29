package com.jms.alertmessaging.dto.auth.sign.in;

import lombok.Data;

@Data
public class SignInRequestDto {
    private String email;
    private String password;
}
