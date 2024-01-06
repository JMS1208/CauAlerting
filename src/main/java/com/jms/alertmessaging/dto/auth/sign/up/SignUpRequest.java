package com.jms.alertmessaging.dto.auth.sign.up;

import lombok.Data;

import java.util.Set;

@Data
public class SignUpRequest {
    String email;
    String password;
    Set<Long> departments;
}
