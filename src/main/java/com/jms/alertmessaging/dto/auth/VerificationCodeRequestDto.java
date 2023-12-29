package com.jms.alertmessaging.dto.auth;

import lombok.Data;

@Data
public class VerificationCodeRequestDto {
    private String username;
    private String recaptchaToken;
}
