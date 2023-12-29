package com.jms.alertmessaging.dto.auth;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

@Data
public class PostSignUpDto {
    private String username;
    private String password;
    private long major;
    private String verificationCode;
}
